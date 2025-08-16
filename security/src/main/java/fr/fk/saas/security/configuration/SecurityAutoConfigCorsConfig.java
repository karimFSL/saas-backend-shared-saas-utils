package fr.fk.saas.security.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@AllArgsConstructor
@Configuration
@Import(SecurityAutoConfigProps.class)
public class SecurityAutoConfigCorsConfig implements WebMvcConfigurer {

    /**
     * Cors model
     */
    private SecurityAutoConfigProps securityAutoConfigProps;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (securityAutoConfigProps.getCors().isStandalone()) {
            registry.addMapping("/**")
                    .allowedOriginPatterns(securityAutoConfigProps.getCors().getAllowedOrigins().toArray(String[]::new))
                    .allowCredentials(securityAutoConfigProps.getCors().isAllowCredentials())
                    .allowedHeaders(securityAutoConfigProps.getCors().getAllowedHeaders().toArray(String[]::new))
                    .allowedMethods(securityAutoConfigProps.getCors().getAllowedMethods().toArray(String[]::new))
                    .exposedHeaders(securityAutoConfigProps.getCors().getAllowedHeaders().toArray(String[]::new));
        }

    }
}
