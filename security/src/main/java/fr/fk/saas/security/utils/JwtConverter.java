package fr.fk.saas.security.utils;


import fr.fk.saas.security.model.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.fk.saas.security.model.Constants.*;

@Slf4j
public class JwtConverter  implements Converter<Jwt, AbstractAuthenticationToken> {


    /**
     * Convertit un JWT en objet AbstractAuthenticationToken.
     *
     * Cette méthode extrait les informations d'autorisation du JWT et crée
     * un token d'authentification avec les rôles et permissions appropriés.
     *
     * @param jwt le token JWT à convertir
     * @return AbstractAuthenticationToken contenant les informations d'authentification
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        if (jwt == null) {
            log.warn("JWT token is null, returning null authentication");
            return null;
        }

        try {
            // Extraction des autorités depuis le JWT
            Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

            // Création du token d'authentification
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities);

            log.debug("Successfully converted JWT for subject: {} with authorities: {}",
                    jwt.getSubject(), authorities);

            return authenticationToken;

        } catch (Exception e) {
            log.error("Error converting JWT to authentication token", e);
            // Retourner un token avec des autorités vides plutôt que null
            return new JwtAuthenticationToken(jwt, Collections.emptyList());
        }
    }


    /**
     * Extrait les autorités (rôles et permissions) depuis les claims du JWT.
     *
     * Cette méthode supporte plusieurs formats de claims d'autorisation :
     * - Keycloak realm_access.roles
     * - Keycloak resource_access.[client].roles
     * - Claim "roles" direct
     * - Claim "authorities" direct
     *
     * @param jwt le token JWT
     * @return Collection des autorités extraites
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Collections.emptyList();

        // 1. Essayer d'extraire depuis realm_access (Keycloak)
        authorities = extractRealmAccessRoles(jwt);
        if (!authorities.isEmpty()) {
            return authorities;
        }

        // 2. Essayer d'extraire depuis resource_access (Keycloak)
        authorities = extractResourceAccessRoles(jwt);
        if (!authorities.isEmpty()) {
            return authorities;
        }

        // 3. Essayer d'extraire depuis le claim "roles"
        authorities = extractDirectRoles(jwt, ROLES_CLAIM);
        if (!authorities.isEmpty()) {
            return authorities;
        }

        // 4. Essayer d'extraire depuis le claim "authorities"
        authorities = extractDirectRoles(jwt, AUTHORITIES_CLAIM);
        if (!authorities.isEmpty()) {
            return authorities;
        }

        log.debug("No authorities found in JWT for subject: {}", jwt.getSubject());
        return Collections.emptyList();
    }


    /**
     * Extrait les rôles depuis realm_access.roles (format Keycloak).
     */
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmAccessRoles(Jwt jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS_CLAIM);
            if (realmAccess != null && realmAccess.containsKey(ROLES_CLAIM)) {
                List<String> roles = (List<String>) realmAccess.get(ROLES_CLAIM);
                return convertRolesToAuthorities(roles);
            }
        } catch (Exception e) {
            log.debug("Error extracting realm access roles: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Extrait les rôles depuis resource_access.[client].roles (format Keycloak).
     */
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractResourceAccessRoles(Jwt jwt) {
        try {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
            if (resourceAccess != null) {
                // Collecte tous les rôles de toutes les ressources
                return resourceAccess.values().stream()
                        .filter(Map.class::isInstance)
                        .map(resource -> (Map<String, Object>) resource)
                        .filter(resource -> resource.containsKey(ROLES_CLAIM))
                        .flatMap(resource -> {
                            List<String> roles = (List<String>) resource.get(ROLES_CLAIM);
                            return convertRolesToAuthorities(roles).stream();
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.debug("Error extracting resource access roles: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Extrait les rôles directement depuis un claim spécifique.
     */
    private Collection<GrantedAuthority> extractDirectRoles(Jwt jwt, String claimName) {
        try {
            List<String> roles = jwt.getClaimAsStringList(claimName);
            if (roles != null && !roles.isEmpty()) {
                return convertRolesToAuthorities(roles);
            }
        } catch (Exception e) {
            log.debug("Error extracting roles from claim '{}': {}", claimName, e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Convertit une liste de rôles en String vers une Collection de GrantedAuthority.
     * Ajoute automatiquement le préfixe "ROLE_" si nécessaire.
     */
    private Collection<GrantedAuthority> convertRolesToAuthorities(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .filter(role -> role != null && !role.trim().isEmpty())
                .map(role -> role.trim())
                .map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
