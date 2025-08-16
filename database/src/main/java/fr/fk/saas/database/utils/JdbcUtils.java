package fr.fk.saas.database.utils;


import fr.fk.saas.database.multitenant.client.dto.TenantDataSourceResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcUtils {

    /**
     * Build jdbc url from tenant datasource
     * @param dto: the tenant datasource
     * @return the jdbc url
     */
    public static String buildJdbcUrl(TenantDataSourceResponseDTO dto) {
        String type = dto.getType();
        String host = dto.getHost();
        String dbName = dto.getName();
        String schema = dto.getSchema();
        return switch (type.toLowerCase()) {
            case "postgresql" -> {
                String url = String.format("jdbc:postgresql://%s/%s", host, dbName);
                if (schema != null && !schema.isBlank()) {
                    url += "?currentSchema=" + schema;
                }
                yield url;
            }
            case "h2" -> String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1", dbName);
            case "mysql" -> String.format("jdbc:mysql://%s/%s", host, dbName);
            case "sqlserver" -> String.format("jdbc:sqlserver://%s;databaseName=%s", host, dbName);
            case "oracle" -> String.format("jdbc:oracle:thin:@%s:%s", host, dbName);
            case "mongodb" -> String.format("mongodb://%s/%s", host, dbName);
            default -> throw new IllegalArgumentException("Unsupported datasource type: " + type);
        };
    }

    /**
     * Returns the JDBC driver class name for a given database type.
     * @param type the datasource type (e.g. postgresql, mysql, sqlserver, oracle, mongodb)
     * @return the fully qualified driver class name
     */
    public static String getDriverClassName(String type) {
        return switch (type.toLowerCase()) {
            case "postgresql" -> "org.postgresql.Driver";
            case "h2" -> "org.h2.Driver";
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "oracle" -> "oracle.jdbc.OracleDriver";
            case "mongodb" -> "mongodb.jdbc.MongoDriver"; // Pour MongoDB JDBC driver (ex: https://github.com/mongodb/mongo-jdbc-driver)
            default -> throw new IllegalArgumentException("Unsupported datasource type: " + type);
        };
    }

}
