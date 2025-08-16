package fr.fk.saas.link.m2m;

import fr.fk.saas.link.exception.M2MBlSecurityTokenGeneratorException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public abstract class M2MClient {

    private final RestClient restClient;
    private final String requestBody;

    protected M2MClient(String username, String password, String clientId, RestClient restClient) {
        validOrThrow(username);
        validOrThrow(password);
        validOrThrow(clientId);

        this.restClient = restClient;
        this.requestBody = buildRequestBody(username, password, clientId);
    }

    private void validOrThrow(String arg) {
        if (!StringUtils.hasText(arg)) {
            throw new IllegalArgumentException(arg + " for technical account (m2m) must be provided and not empty");
        }
    }

    public String buildRequestBody(String username, String password, String clientId) {
        return "grant_type=password&username=%s&password=%s&client_id=%s"
                .formatted(username, password, clientId);
    }

    public String generateAccessToken() throws M2MBlSecurityTokenGeneratorException {
        try {
            var res = restClient
                    .post()
                    .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                    .body(requestBody)
                    .retrieve()
                    .body(TokenResponse.class);
            if (res == null) {
                throw new RuntimeException("JWT Token: The response entity is null, impossible to extract access token.");
            }
            return res.getAccessToken();
        } catch (Exception e) {
            throw new M2MBlSecurityTokenGeneratorException("An error occurred while retrieving a JWT from BL Security", e);
        }
    }

}