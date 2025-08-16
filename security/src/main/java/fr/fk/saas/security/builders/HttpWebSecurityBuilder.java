package fr.fk.saas.security.builders;


import fr.fk.saas.security.configuration.SecurityAutoConfigProps;
import fr.fk.saas.security.model.Constants;
import fr.fk.saas.security.utils.CustomAuthorizationManager;
import fr.fk.saas.security.utils.JwtConverter;
import fr.fk.saas.security.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static fr.fk.saas.security.model.Constants.SECURITY_WHITE_LIST;
import static org.springframework.security.config.Customizer.withDefaults;

@AllArgsConstructor
public class HttpWebSecurityBuilder {

    /**
     * Security model
     */
    private SecurityAutoConfigProps props;


    public void buildHttpSecurity(HttpSecurity http, JwtDecoder jwtDecoder, JwtConverter jwtConverter, CustomAuthorizationManager authorizationManager) throws Exception {
        //@formatter:off
        http.csrf(CsrfConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    auth.requestMatchers(SecurityUtils.toPathPatternMatchers(SECURITY_WHITE_LIST)).permitAll();
                    auth.requestMatchers(SecurityUtils.toPathPatternMatchers(props.getSecurityWhiteList())).permitAll();

             //       auth.anyRequest().access(authorizationManager);
                })
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter)
                                .decoder(jwtDecoder))
                )
                .httpBasic(withDefaults())
                .oauth2Client(withDefaults())
                .headers(h -> h
                        .xssProtection(withDefaults())
                        .contentSecurityPolicy( contentSecurityCustomizer -> contentSecurityCustomizer
                                .policyDirectives(getCspDirectives()))
                );
        //@formatter:on
    }

    /**
     * Retourne les directives CSP (Content Security Policy)
     * Utilise la configuration personnalisée ou la valeur par défaut
     */
    private String getCspDirectives() {
        return StringUtils.isNotBlank(props.getCsp()) ?
                props.getCsp() :
                Constants.DEFAULT_CSP;
    }
}
