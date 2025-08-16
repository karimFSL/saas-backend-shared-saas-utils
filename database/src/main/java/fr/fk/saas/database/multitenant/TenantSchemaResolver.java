package fr.fk.saas.database.multitenant;

import fr.fk.saas.database.configuration.DatabaseAutoConfigProps;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


@RequiredArgsConstructor
public class TenantSchemaResolver implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

    /**
     * DataSource
     */
    private final DataSource dataSource;

    /**
     * Config props
     */
    private final DatabaseAutoConfigProps props;

    /**
     * @param tenantId: the tenant id
     * @return the connection with the tenant schema
     * @throws SQLException: the SQL exception
     */
    @Override
    public Connection getConnection(String tenantId) throws SQLException {
        final Connection connection = getAnyConnection();
        connection.setSchema(tenantId);
        return connection;
    }

    /**
     * @return the connection available from the pool
     * @throws SQLException: the SQL exception
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Release the connection
     *
     * @param connection: the connection to release
     * @throws SQLException: the SQL exception
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Release the connection for a specific tenant
     *
     * @param s: the tenant id
     */
    @Override
    public void releaseConnection(String s, Connection connection) throws SQLException {
        releaseAnyConnection(connection);
    }

    /**
     * Check if the connection provider supports multi-tenancy
     *
     * @return true if multi-tenancy is supported, false otherwise
     */
    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }


    public boolean isUnwrappableAs(@NonNull Class<?> aClass) {
        return false;
    }


    @Override
    public <T> T unwrap(@NonNull Class<T> aClass) {
        return null;
    }


    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
