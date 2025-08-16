package fr.fk.saas.database;

import fr.fk.saas.database.props.DatabaseAutoConfigProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@ComponentScan(basePackages = {"fr.fk.saas.database.*"})
@EnableConfigurationProperties({DatabaseAutoConfigProps.class})
@Import({DatabaseAutoConfig.class})
public @interface DatabaseAutoConfig {

    boolean enabled() default true;
}
