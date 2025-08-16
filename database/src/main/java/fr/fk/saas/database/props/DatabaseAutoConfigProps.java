package fr.fk.saas.database.props;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "autoconfig.database")
@AllArgsConstructor
@Data
@Builder
public class DatabaseAutoConfigProps {
}
