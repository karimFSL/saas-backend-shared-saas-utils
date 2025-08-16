package fr.fk.saas.security;


import fr.fk.saas.security.configuration.SecurityAutoConfigProps;
import fr.fk.saas.security.configuration.SecurityAutoConfigImportSelector;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@ComponentScan(basePackages = {"fr.fk.saas.security.*"})
@EnableConfigurationProperties({SecurityAutoConfigProps.class})
@Import({SecurityAutoConfigImportSelector.class})
public @interface SecurityAutoConfig {
    boolean enabled() default true;
}
