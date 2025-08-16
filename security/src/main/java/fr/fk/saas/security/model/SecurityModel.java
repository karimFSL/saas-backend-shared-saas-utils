/**
 * 
 */
package fr.fk.saas.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@Getter
@Setter
@Builder
public class SecurityModel {

    private List<String> audience;

    private String issuer;

    private String jwkUri;

    private String adminTenantCode;

    private String[] authorizationWhiteList;

    private List<String> rules;
}
