package fr.fk.saas.database.configuration;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "autoconfig.database")
@AllArgsConstructor
@Data
@Builder
public class DatabaseAutoConfigProps {

    private String serviceId;

    private MultitenancyStrategy multitenancy;

    private ProviderProps provider;

    private List<String> packagesToScan;

    public enum MultitenancyStrategy {
        DEFAULT,
        TENOX,
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProviderProps {
        private String url;
        private String apiKey;
        private String decryptionKey;
    }
}
