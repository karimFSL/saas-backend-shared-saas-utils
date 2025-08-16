package fr.fk.saas.database.multitenant;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import fr.fk.saas.core.encryption.Encryption;
import fr.fk.saas.core.model.Context;
import fr.fk.saas.core.model.ContextHolder;
import fr.fk.saas.database.configuration.DatabaseAutoConfigProps;
import fr.fk.saas.database.multitenant.client.ApiTenoxClient;
import fr.fk.saas.database.multitenant.client.dto.ConfigDTO;
import fr.fk.saas.database.multitenant.client.dto.TenantDataSourceResponseDTO;
import fr.fk.saas.database.utils.JdbcUtils;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Log4j2
public class TenoxDataSourceResolver  extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {

    private final transient ApiTenoxClient apiTenoxClient;

    private final transient DatabaseAutoConfigProps props;

    private final transient SpringLiquibase liquibaseProperties;

    private final transient Cache<String, DataSource> dataSourceCache = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(500)
            .build();


    @Override
    protected DataSource selectAnyDataSource() {
        return null;
    }


    private String getKey() {
        Context ctx = ContextHolder.getCurrentContext();
        return String.join(":", ctx.getAppId(), ctx.getServiceId(), ctx.getTenantId());
    }

    /**
     * Determine target datasource
     * @return the datasource
     */
    @Override
    @NonNull
    protected DataSource selectDataSource(String tenantId) {
        Context ctx = ContextHolder.getCurrentContext();
        ConfigDTO config = apiTenoxClient.getConfig(ctx.getTenantId(), ctx.getAppId(),
                ctx.getServiceId(), props.getProvider().getApiKey());

        if (config.getDataSource() == null) {
            log.error("No datasource found for this context {}-{}-{}", ctx.getTenantId(), ctx.getAppId(),
                    props.getServiceId());
            throw new RuntimeException("No datasource found for this context " + ctx.getTenantId() + "-" + ctx.getAppId() + "-" + props.getServiceId());
        }
        String key = getKey();

        return Objects.requireNonNull(dataSourceCache.get(Objects.requireNonNull(key), k -> {
            DataSource dataSource = buildDataSource(config.getDataSource());

            if (liquibaseProperties != null) {
                try {
                    runLiquibase(dataSource);
                } catch (LiquibaseException e) {
                    throw new RuntimeException("Error during liquibase execution for tenant "
                            + ctx.getTenantId() + " and app " + ctx.getAppId() + " and component " + props.getServiceId()
                            , e);
                }
            }


            return dataSource;
        }));
    }

    /**
     * Build datasource from tenant datasource response
     * @param tenantDataSourceResponseDTO: the tenant datasource
     * @return the datasource
     */
    private DataSource buildDataSource(TenantDataSourceResponseDTO tenantDataSourceResponseDTO) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(JdbcUtils.buildJdbcUrl(tenantDataSourceResponseDTO));
        ds.setUsername(Encryption.decrypt(tenantDataSourceResponseDTO.getEncryptedUsername(),
                props.getProvider().getDecryptionKey()));
        ds.setPassword(Encryption.decrypt(tenantDataSourceResponseDTO.getEncryptedPassword(), props.getProvider().getDecryptionKey()));
        ds.setPoolName(tenantDataSourceResponseDTO.getName());
        ds.setMaximumPoolSize(1);
        ds.setDriverClassName(JdbcUtils.getDriverClassName(tenantDataSourceResponseDTO.getType()));
        if (tenantDataSourceResponseDTO.getSchema() != null) ds.setSchema(tenantDataSourceResponseDTO.getSchema());

        return ds;
    }


    private void runLiquibase(DataSource ds) throws LiquibaseException {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(ds);
        liquibase.setResourceLoader(liquibaseProperties.getResourceLoader());
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.afterPropertiesSet();
    }


}
