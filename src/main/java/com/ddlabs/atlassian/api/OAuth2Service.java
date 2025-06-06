package com.ddlabs.atlassian.api;

import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.impl.exception.AuthenticationException;
import com.ddlabs.atlassian.impl.exception.NullOrEmptyFieldsException;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Service interface for OAuth2 operations.
 */
public interface OAuth2Service {
    
    /**
     * Generates an authorization URL for the OAuth2 flow.
     *
     * @param config The OAuth2 configuration
     * @return The authorization URL
     * @throws AuthenticationException If an error occurs
     */
    String generateAuthorizationUrl(OAuth2Configuration config) throws AuthenticationException;
    
    /**
     * Exchanges an authorization code for an access token.
     *
     * @param code The authorization code
     * @param config The OAuth2 configuration
     * @return The OAuth2 token response
     * @throws AuthenticationException If an error occurs
     */
    OAuth2TokenResponse exchangeCodeForTokens(String code, OAuth2Configuration config) throws AuthenticationException;
    /**
     * Checks if an access token is expired.
     *
     * @param accessTokenExpiry The access token expiry timestamp
     * @return True if the token is expired, false otherwise
     */
    boolean isAccessTokenExpired(long accessTokenExpiry);
    
    /**
     * Checks if a token response is expired.
     *
     * @param tokenResponse The OAuth2 token response
     * @return True if the token is expired, false otherwise
     */
    boolean isTokenExpired(OAuth2TokenResponse tokenResponse);
    Logger log = LoggerFactory.getLogger(OAuth2AuthorizationService.class.getName());
    default String buildRefreshTokenUrl(String grantType, String refreshToken,  String clientKey,
                                        String clientSecrete){
        return  "grant_type=" + URLEncoder.encode(grantType, StandardCharsets.UTF_8) +
                "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8) +
                "&client_id=" + URLEncoder.encode(clientKey, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(clientSecrete, StandardCharsets.UTF_8);
    }

    /**
     * Constructs the URL parameters for the authorization code exchange.
     *
     * @param clientId     The client ID.
     * @param clientSecret The client secret.
     * @param redirectUri  The redirect URI.
     * @param code         The authorization code.
     * @param codeVerifier The code verifier.
     * @return The URL parameters as a string.
     */
    default String constAuthorizationCodeForAccessTokenUrl(@NotNull final String  clientId,
                                                           @NotNull final String clientSecret,
                                                           @NotNull final String redirectUri,
                                                           @NotNull final String code,
                                                           @NotNull final String codeVerifier) {
        try{
            ValidationUtils.checkNotNullOrEmptyStrings(clientId,clientSecret,redirectUri,code,codeVerifier);
            return "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                    "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                    "&redirect_uri=" + redirectUri +
                    "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                    "&code_verifier=" + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8) +
                    "&grant_type=authorization_code";
        }catch (NullOrEmptyFieldsException e ) {
            throw new RuntimeException("Error building authorization code for access token URL: " + e.getMessage(), e);
        }
    }
    /**
     * Exchanges the refresh token for a new access token.
     *
     * @param grantType        The grant type (e.g., "refresh_token").
     * @param refreshToken     The refresh token.
     * @param clientKey        The client key.
     * @param clientSecrete    The client secret.
     * @param tokenEndpoint    The endpoint of the authorization server.
     * @return The access token response as a string.
     */
    String exchangeRefreshTokenForAccessToken(String grantType, String refreshToken,  String clientKey,
                                              String clientSecrete, String tokenEndpoint) throws Exception;

    /**
     * Refreshes the OAuth2 token for a specific server type.
     * @param metricServer The MetricServer instance representing the server for which the token needs to be refreshed.
     */
    void refreshToken(MetricServer metricServer) throws Exception;
    default boolean isAccessTokenExpired(Long accessTokenExpiry) {
        Assert.notNull(accessTokenExpiry, "Access token expiry time cannot be null");
        return Instant.now().getEpochSecond() >= accessTokenExpiry;
    }
}
