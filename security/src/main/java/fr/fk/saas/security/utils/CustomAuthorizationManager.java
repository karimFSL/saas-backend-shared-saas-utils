package fr.fk.saas.security.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AccessMap accessMap;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();

        // Si non authentifié => refus
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Récupération des rôles attendus pour cette URL
        List<String> rolesAllowed = accessMap.get(context.getRequest());
        if (rolesAllowed == null || rolesAllowed.isEmpty()) {
            // Aucun mapping => refus par défaut
            return new AuthorizationDecision(false);
        }

        // Vérification si l'utilisateur possède au moins un rôle autorisé
        boolean granted = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rolesAllowed::contains);

        return new AuthorizationDecision(granted);
    }
}
