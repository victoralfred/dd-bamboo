package com.ddlabs.atlassian.api;

import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.util.HelperUtil;
import com.ddlabs.atlassian.exception.NullOrEmptyFieldsException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;


public interface OAuth2AuthorizationService {
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
            HelperUtil.checkNotNullOrEmptyStrings(clientId,clientSecret,redirectUri,code,codeVerifier);
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
