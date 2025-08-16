package fr.fk.saas.security.utils;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import fr.fk.saas.security.model.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {


    /**
     * Transform list of paths to custom RequestMatcher array using PathPatternParser.
     *
     * @param pathList the pathList
     * @return a RequestMatcher array
     */
    public static RequestMatcher[] toPathPatternMatchers(String[] pathList) {
        if (pathList == null || pathList.length == 0) {
            return new RequestMatcher[]{};
        }
        PathPatternParser parser = new PathPatternParser();
        return Arrays.stream(pathList)
                .map(pattern -> {
                    PathPattern pathPattern = parser.parse(pattern);
                    return (RequestMatcher) request
                            -> pathPattern.matches(PathContainer.parsePath(request.getRequestURI()));
                })
                .toArray(RequestMatcher[]::new);
    }

    /**
     * Transform list of paths to custom RequestMatcher array using PathPatternParser.
     *
     * @param pathList the pathList
     * @return a RequestMatcher array
     */
    public static RequestMatcher[] toPathPatternMatchers(List<String> pathList) {
        if (pathList == null || pathList.isEmpty()) {
            return new RequestMatcher[]{};
        }
        PathPatternParser parser = new PathPatternParser();
        return pathList.stream()
                .map(pattern -> {
                    PathPattern pathPattern = parser.parse(pattern);
                    return (RequestMatcher) request
                            -> pathPattern.matches(PathContainer.parsePath(request.getRequestURI()));
                })
                .toArray(RequestMatcher[]::new);
    }
    /**
     * @param jwt: the jwt as string
     * @return the nimbus JWT object
     */
    public static JWT parse(String jwt) {
        try {
            return JWTParser.parse(jwt);
        } catch (Exception e) {
            throw new SecurityException("Error parsing JWT {}", e);
        }
    }


    /**
     * @param bearerToken : the bearerToken as string
     * @return the token without "Bearer" keyword
     */
    public static String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.startsWith(Constants.HEADER_PREFIX_BEARER)) {
                return bearerToken.substring(7).trim();
            } else {
                // maybe the token has already been extracted
                return bearerToken;
            }

        } else {
            log.warn("Careful ! No token found in header Authorization");
        }
        return null;
    }


    /**
     * Guess currentUser
     */
    public static JWT getCurrentUserJWT() {
        var token = getCurrentUserOauthJwt();
        if (token != null) {
            return parse(resolveToken(token.getTokenValue()));
        } else {
            log.error("Could not find current user JWT");
        }
        return null;
    }

    public static Jwt getCurrentUserOauthJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        } else {
            throw new SecurityException("There is no user connected");
        }
    }
}
