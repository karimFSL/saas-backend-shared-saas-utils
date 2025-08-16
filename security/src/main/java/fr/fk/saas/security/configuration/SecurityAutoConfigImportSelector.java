package fr.fk.saas.security.configuration;

import fr.fk.saas.security.SecurityAutoConfig;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;


@NoArgsConstructor
public class SecurityAutoConfigImportSelector implements ImportSelector {

    public String[] selectImports(AnnotationMetadata metadata) {
        boolean enabled = true;
        Map<String, Object> attributes = metadata.getAnnotationAttributes(SecurityAutoConfig.class.getName());
        if (attributes != null) {
            enabled = (Boolean)attributes.get("enabled");
        }

        return enabled ? new String[] {
                SecurityAutoConfigProps.class.getName(),
        } : new String[0];
    }

}
