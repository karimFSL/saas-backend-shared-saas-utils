package fr.fk.saas.database.multitenant.client;


import fr.fk.saas.database.multitenant.client.dto.ConfigDTO;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;



@Retry(name = "api-tenox-client")
public interface ApiTenoxClient {

    @GetMapping(value = "/config", produces = "application/json")
    @Cacheable(
            value = "tenox-config",
            key = "#tenantId + ':' + #appId",
            unless = "#result == null || #result.dataSource == null"
    )
    ConfigDTO getConfig(@RequestParam("id") String tenantId,
                        @RequestParam("appId") String appId,
                        @RequestParam("serviceId") String serviceId,
                        @RequestHeader("X-API-KEY") String apiKey);

}
