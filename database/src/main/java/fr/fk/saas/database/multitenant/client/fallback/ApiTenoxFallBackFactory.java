package fr.fk.saas.database.multitenant.client.fallback;


import fr.fk.saas.database.multitenant.client.ApiTenoxClient;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.cloud.openfeign.FallbackFactory;


public class ApiTenoxFallBackFactory implements FallbackFactory<ApiTenoxClient> {


    @Override
    public ApiTenoxClient create(Throwable cause) {
        return (tenantId, appId, serviceId, apiKey) -> {
            ParameterizedMessage message = new ParameterizedMessage("Error during retrieval of tenant config"
                    + " of tenant {} from app {} and service{}. Cause : {}", tenantId, appId,
                    serviceId, cause.getMessage());
            throw new RuntimeException(message.getFormattedMessage(), cause);
        };
    }
}
