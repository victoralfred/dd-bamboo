package com.ddlabs.atlassian.auth;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.api.HttpConnectionFactory;
import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.metrics.model.MSConfig;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.util.HelperUtil;
import com.ddlabs.atlassian.util.exceptions.NullOrEmptyFieldsException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@BambooComponent
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthorizationServiceImpl.class);
    private final HttpConnectionFactory connectionFactory;
    private final PluginDaoRepository pluginDaoRepository;
    private final UserService userService;
    public OAuth2AuthorizationServiceImpl(HttpConnectionFactory connectionFactory, PluginDaoRepository pluginDaoRepository, UserService userService) {
        this.connectionFactory = connectionFactory;
        this.pluginDaoRepository = pluginDaoRepository;
        this.userService = userService;
    }
    @Override
    public String exchangeAuthorizationCodeForAccessToken(@NotNull final String redirectUri,
                                                          @NotNull final String clientId,
                                                          @NotNull final String clientSecret,
                                                          @NotNull final String grantType,
                                                          @NotNull final String codeVerifier,
                                                          @NotNull final String code,
                                                          @NotNull final String tokenEndpoint ) {
        try {
            HelperUtil.checkNotNullOrEmptyStrings(redirectUri,clientId,clientSecret,codeVerifier,code,tokenEndpoint);
            String urlParameters = buildAuthorizationAccessURL(clientId, clientSecret,
                    redirectUri, code, codeVerifier);
            HttpsURLConnection con = (HttpsURLConnection) connectionFactory.getUrlConnection(
                    new URI(tokenEndpoint), urlParameters
            ,"POST","application/x-www-form-urlencoded");
            return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException | URISyntaxException | NullOrEmptyFieldsException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String exchangeAuthorizationCodeForAccessToken(String grantType, String refreshToken, String clientKey,
                                                          String clientSecrete, String tokenEndpoint) throws Exception {
        try {
            HelperUtil.checkNotNullOrEmptyStrings(grantType, refreshToken, clientKey, clientSecrete, tokenEndpoint);
            String urlParameters = buildRefreshTokenUrl(grantType, refreshToken, clientKey, clientSecrete);
            HttpsURLConnection con = (HttpsURLConnection) connectionFactory.getUrlConnection(
                    new URI(tokenEndpoint), urlParameters
            ,"POST","application/x-www-form-urlencoded");
            return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException | NullOrEmptyFieldsException e) {
            throw new RuntimeException("Error exchanging authorization code for access token: " + e.getMessage(), e);
        }
    }
    // Check if the access token is expired, and if so, retry a request to exchange new access token with refresh token
    @Override
    public void refreshToken(MetricServer metricServer) throws Exception {
        Assert.notNull(metricServer, "Server type cannot be null");
        String serverType = metricServer.getClass().getSimpleName();
        // Get the server configuration from the database
        // Get the class name of the MetricServer implementation
        MSConfig msConfig = pluginDaoRepository.getServerConfigByType(serverType);
        if (msConfig == null) {
            log.warn("No configuration found for server: {}", serverType);
            return;
        }
        // Check if the access token is expired
        if (isAccessTokenExpired(msConfig.getAccessTokenExpiry())) {
            try {
                String newAccessToken = exchangeAuthorizationCodeForAccessToken(
                        "refresh_token",
                        userService.decrypt(msConfig.getRefreshToken()),
                        msConfig.getClientId(),
                        userService.decrypt(msConfig.getClientSecret()),
                        msConfig.getTokenEndpoint()
                );
                // Update the access token in the database or cache
                msConfig.setAccessToken(newAccessToken);
                pluginDaoRepository.updateServerConfig(msConfig);
            } catch (Exception e) {
                log.error("Failed to refresh access token for server: {}", serverType, e);
                throw new Exception("Failed to refresh access token for server: " + serverType, e);
            }
        }
       // Get access token from the database or cache
        List<MSConfig> msConfigs = pluginDaoRepository.getMsConfig();
        for (MSConfig config : msConfigs) {
            if (isAccessTokenExpired(config.getAccessTokenExpiry())) {
                try {
                    String newAccessToken = exchangeAuthorizationCodeForAccessToken(
                            config.getRedirectUrl(),
                            config.getClientId(),
                            config.getClientSecret(),
                            "authorization_code",
                            config.getCodeVerifier(),
                            config.getAccessToken(),
                            config.getTokenEndpoint()
                    );
                    // Update the access token in the database or cache
                    log.info("Refreshed access token for server: {}", newAccessToken);
                } catch (Exception e) {
                    log.error("Failed to refresh access token for server: {}", config.getServerName(), e);
                    throw new Exception("Failed to refresh access token for server: " + config.getServerName(), e);
                }
            }
        }

    }
    /**
     * Builds the authorization access URL for exchanging the authorization code for an access token.
     *
     * @param clientId       The client ID of the application.
     * @param clientSecret   The client secret of the application.
     * @param redirectUri    The redirect URI to which the authorization server will send the user after authorization.
     * @param code           The authorization code received from the authorization server.
     * @param codeVerifier   The code verifier used in PKCE (Proof Key for Code Exchange).
     * @return A string representing the complete URL with parameters for exchanging the authorization code for an access token.
     */
    private String buildAuthorizationAccessURL(@NotNull final String clientId,
                                               @NotNull final String clientSecret,
                                               @NotNull final String redirectUri,
                                               @NotNull final String code,
                                               @NotNull final String codeVerifier) {
        return constAuthorizationCodeForAccessTokenUrl(clientId, clientSecret,
                redirectUri, code, codeVerifier);
    }
}
