package fr.fk.saas.security.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SecurityAutoConfigProps.class)
public class SecurityAutoConfigOpenAPIConfig {


    private static final String DEFAULT_SECURITY_SCHEME = "oauth2";
    private static final String DEFAULT_AUTHORIZATION_URL = "https://default-authorization.url";
    private static final String DEFAULT_TOKEN_URL = "https://default-token.url";

    private final SecurityAutoConfigProps securityProps;

    public SecurityAutoConfigOpenAPIConfig(SecurityAutoConfigProps securityProps) {
        this.securityProps = securityProps;
    }

    @Bean
    public OpenAPI customizeOpenAPI() {
        SecurityAutoConfigProps.OpenAPIProps openAPI = securityProps.getCustomAuthProps() != null
                ? securityProps.getCustomAuthProps().getRules() != null && !securityProps.getCustomAuthProps().getRules().isEmpty()
                ? new SecurityAutoConfigProps.OpenAPIProps() // placeholder si besoin
                : null
                : null;

        // Récupération des valeurs OpenAPI depuis les propriétés
        String securitySchemeName = (openAPI != null && openAPI.getSecuritySchemeName() != null)
                ? openAPI.getSecuritySchemeName()
                : "oauth2";
        String authorizationUrl = (openAPI != null && openAPI.getAuthorizationUrl() != null)
                ? openAPI.getAuthorizationUrl()
                : "https://default-authorization.url";
        String tokenUrl = (openAPI != null && openAPI.getTokenUrl() != null)
                ? openAPI.getTokenUrl()
                : "https://default-token.url";

        // Contact
        Contact contact = new Contact();
        if (openAPI != null && openAPI.getContact() != null) {
            SecurityAutoConfigProps.OpenAPIContact apiContact = openAPI.getContact();
            contact.setEmail(apiContact.getEmail());
            contact.setName(apiContact.getName());
            contact.setUrl(apiContact.getUrl());
        }

        // License
        License license = new License();
        if (openAPI != null && openAPI.getInfo() != null && openAPI.getInfo().getLicense() != null) {
            SecurityAutoConfigProps.OpenAPILicense apiLicense = openAPI.getInfo().getLicense();
            license.setName(apiLicense.getName());
            license.setUrl(apiLicense.getUrl());
        }

        // Info
        Info info = new Info();
        if (openAPI != null && openAPI.getInfo() != null) {
            SecurityAutoConfigProps.OpenAPIInfo apiInfo = openAPI.getInfo();
            info.setTitle(apiInfo.getTitle());
            info.setDescription(apiInfo.getDescription());
            info.setVersion(apiInfo.getVersion());
            info.setTermsOfService(apiInfo.getTermsOfServiceUrl());
            info.setLicense(license);
            info.setContact(contact);
        }

        // Création du bean OpenAPI
        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl(authorizationUrl)
                                                        .tokenUrl(tokenUrl)
                                                )
                                        )
                        )
                );
    }
}
