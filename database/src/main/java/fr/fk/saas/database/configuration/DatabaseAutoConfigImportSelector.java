package fr.fk.saas.database.configuration;

import fr.fk.saas.database.DatabaseAutoConfig;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

@NoArgsConstructor
public class DatabaseAutoConfigImportSelector implements ImportSelector {

    /**
     * Select the imports to use when enabling this annotation
     *
     * @param metadata : the annotation metadata
     * @return the array of classes to import
     */
    public String[] selectImports(AnnotationMetadata metadata) {
        boolean enabled = true;
        Map<String, Object> attributes = metadata.getAnnotationAttributes(DatabaseAutoConfig.class.getName());
        if (attributes != null) {
            enabled = (Boolean)attributes.get("enabled");
        }

        return enabled ? new String[] {
                DatabaseAutoConfigProps.class.getName(),
        } : new String[0];
    }

}
