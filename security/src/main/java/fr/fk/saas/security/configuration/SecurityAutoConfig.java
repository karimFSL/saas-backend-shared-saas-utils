package fr.fk.saas.security.configuration;


import fr.fk.saas.security.builders.HttpWebSecurityBuilder;
import fr.fk.saas.security.builders.JwtDecoderBuilder;
import fr.fk.saas.security.filters.XSSFilter;
import fr.fk.saas.security.model.SecurityModel;
import fr.fk.saas.security.utils.AccessMap;
import fr.fk.saas.security.utils.CustomAuthorizationManager;
import fr.fk.saas.security.utils.JwtConverter;
import jakarta.servlet.Filter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(SecurityAutoConfigProps.class)
@AllArgsConstructor
public class SecurityAutoConfig {

    private final SecurityAutoConfigProps securityAutoConfigProps;


    @Bean
    Filter xssFilter() {
        return new XSSFilter();
    }

    @Bean
    public JwtConverter jwtConverter() {
        return new JwtConverter();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoderBuilder.buildJwtDecoder(initSecurityModel());
    }

    @Bean
    public CustomAuthorizationManager authorizationManager() {
        var accessMap = new AccessMap();
      //  accessMap.put("/v1/api/**", List.of("USER", "ADMIN"));
        return new CustomAuthorizationManager(accessMap);
    }

    @Bean
    public HttpWebSecurityBuilder httpWebSecurityBuilder() {
        return new HttpWebSecurityBuilder(securityAutoConfigProps);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtDecoder jwtDecoder,
                                           HttpWebSecurityBuilder securityBuilder) throws Exception {
        securityBuilder.buildHttpSecurity(http, jwtDecoder, jwtConverter(), authorizationManager());
        return http.build();
    }


    /**
     * Init Security model
     */
    private SecurityModel initSecurityModel() {
        return SecurityModel.builder()
                .audience(securityAutoConfigProps.getAuth().getAudience())
                .issuer(securityAutoConfigProps.getAuth().getAuthUrl() + "/auth/realms/saas")
                .jwkUri(securityAutoConfigProps.getAuth().getAuthUrl() + "/auth/realms/saas/protocol/openid" +
                        "-connect/certs")
                .build();
    }
}
