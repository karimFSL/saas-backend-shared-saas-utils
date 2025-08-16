package fr.fk.saas.security.configuration;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "autoconfig.security")
@Data
@Builder
@AllArgsConstructor
public class SecurityAutoConfigProps {

    private CorsProps cors;
    private List<String> securityWhiteList;
    private List<RequestMatcherModel> rules;
    private AuthProps auth;
    private String csp;
    private CustomAuthProps customAuthProps;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CorsProps {
        private boolean standalone;
        private boolean allowCredentials;
        private List<String> allowedOrigins;
        private List<String> allowedHeaders;
        private List<String> allowedMethods;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthProps {
        private String authUrl;
        private String clientId;
        private String clientSecret;
        private List<String> audience;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomAuthProps {
        private String authUrl;
        private List<AuthRule> rules;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthRule {
        private List<String> method;
        private String path;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestMatcherModel {
        String[] uris;
        String[] authorities;
        String[] roles;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenAPIProps {
        private OpenAPIContact contact;
        private OpenAPIInfo info;
        private String securitySchemeName;
        private String authorizationUrl;
        private String tokenUrl;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenAPIContact {
        private String email;
        private String name;
        private String url;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenAPILicense {
        private String name;
        private String url;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenAPIInfo{
        private OpenAPILicense license;
        private String description;
        private String title;
        private String version;
        private String termsOfServiceUrl;
    }
}
