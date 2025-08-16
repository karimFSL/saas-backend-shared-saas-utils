/**
 * 
 */
package fr.fk.saas.security.builders;

import fr.fk.saas.security.model.SecurityModel;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

public class JwtDecoderBuilder {

    /**
     * 
     */
    private JwtDecoderBuilder() {
        throw new IllegalAccessError("builder static class");
    }

    /**
     * Build a web jwt Decoder
     *
     * @return
     */
    public static JwtDecoder buildJwtDecoder(SecurityModel securityModel) {
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(securityModel.getIssuer());
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer);

        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(securityModel.getIssuer());
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

}
