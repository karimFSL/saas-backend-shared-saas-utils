package fr.fk.saas.security.model;

public class Constants {

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24;
    public static final String HEADER_PREFIX_BEARER = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String ROLES_CLAIM = "roles";
    public static final String REALM_ACCESS_CLAIM = "realm_access";
    public static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    public static final String AUTHORITIES_CLAIM = "authorities";
    public static final String ROLE_PREFIX = "ROLE_";


    /**
     * Security white list. Be aware to use SecurityUtils.toAntPath if you want to use it in
     * SecurityFilterChain
     */
    public static final String[] SECURITY_WHITE_LIST = {
            "/v3/**",
            "/swagger-ui/**",
            "/actuator/health/ping"
    };

    /**
     * Filters WhiteList
     */
    public static final String[] DEFAULT_FILTERS_WHITE_LIST = {
            "/actuator/health/**"
    };


    /**
     * client registry provider name
     */
    public static final String SAAS_PROVIDER = "saas";

    public static final String DEFAULT_CSP = "img-src 'self'; script-src 'self'";
}
