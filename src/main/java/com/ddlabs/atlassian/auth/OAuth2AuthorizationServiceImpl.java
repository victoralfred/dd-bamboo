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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static com.ddlabs.atlassian.util.HelperUtil.getJsonString;

@BambooComponent
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthorizationServiceImpl.class);
    private final HttpConnectionFactory connectionFactory;
    private final PluginDaoRepository pluginDaoRepository;
    private final UserService userService;
    
    public OAuth2AuthorizationServiceImpl(HttpConnectionFactory connectionFactory, 
                                         PluginDaoRepository pluginDaoRepository, 
                                         UserService userService) {
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
                                                         @NotNull final String tokenEndpoint) {
        try {
            HelperUtil.checkNotNullOrEmptyStrings(redirectUri, clientId, clientSecret, codeVerifier, code, tokenEndpoint);
            String urlParameters = constAuthorizationCodeForAccessTokenUrl(clientId, clientSecret, redirectUri, code, codeVerifier);
            HttpsURLConnection connection = createHttpsConnection(tokenEndpoint, urlParameters);
            checkResponseCode(connection);
            return readResponseBody(connection);
        } catch (IOException | URISyntaxException | NullOrEmptyFieldsException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String exchangeRefreshTokenForAccessToken(String grantType, 
                                                   String refreshToken, 
                                                   String clientKey,
                                                   String clientSecrete, 
                                                   String tokenEndpoint) throws Exception {
        try {
            HelperUtil.checkNotNullOrEmptyStrings(grantType, refreshToken, clientKey, clientSecrete, tokenEndpoint);
            String urlParameters = buildRefreshTokenUrl(grantType, refreshToken, clientKey, clientSecrete);
            HttpsURLConnection connection = createHttpsConnection(tokenEndpoint, urlParameters);
            checkResponseCode(connection);
            return readResponseBody(connection);
        } catch (IOException | URISyntaxException | NullOrEmptyFieldsException e) {
            throw new RuntimeException(e);
        }
    }
    
    private HttpsURLConnection createHttpsConnection(String endpoint, String urlParameters) 
            throws URISyntaxException, IOException {
        return connectionFactory.openHttpsConnection(
                new URI(endpoint),
                urlParameters, 
                "POST", 
                "application/x-www-form-urlencoded"
        );
    }
    
    private String readResponseBody(HttpsURLConnection connection) throws IOException {
        return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private void checkResponseCode(HttpsURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 200:
                log.info("Successfully exchanged authorization code for access token.");
                break;
            case 400:
                throw new IOException(String.format("Bad request: The request was invalid or cannot be otherwise served. Response code: %d", responseCode));
            case 401:
                throw new IOException(String.format("Unauthorized: Access token is invalid or expired. %d", responseCode));
            case 403:
                log.error("Forbidden: You do not have permission to access this resource.");
                throw new IOException("Forbidden: You do not have permission to access this resource.");
            default:
                log.error("Unexpected response code: {}", responseCode);
        }
    }
    
    @Override
    public void refreshToken(MetricServer metricServer) throws Exception {
        Assert.notNull(metricServer, "Server type cannot be null");
        String serverType = metricServer.getClass().getSimpleName();
        
        MSConfig msConfig = pluginDaoRepository.getServerConfigByType(serverType);
        if (msConfig == null) {
            log.warn("No configuration found for server: {}", serverType);
            return;
        }
        
        if (isAccessTokenExpired(msConfig.getAccessTokenExpiry())) {
            refreshAndUpdateToken(msConfig, serverType);
        } else {
            log.info("Access token for server {} is still valid.", serverType);
        }
    }
    
    private void refreshAndUpdateToken(MSConfig msConfig, String serverType) throws Exception {
        try {
            String response = exchangeRefreshTokenForAccessToken(
                    "refresh_token",
                    msConfig.getRefreshToken(),
                    msConfig.getClientId(),
                    userService.decrypt(msConfig.getClientSecret()),
                    msConfig.getTokenEndpoint()
            );
            
            updateConfigWithNewTokens(msConfig, response);
            pluginDaoRepository.updateServerConfig(msConfig);
        } catch (Exception e) {
            log.error("Failed to refresh access token for server: {}", serverType, e);
            throw new Exception("Failed to refresh access token for server: " + serverType, e);
        }
    }
    
    private void updateConfigWithNewTokens(MSConfig msConfig, String response) {
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        int expiresIn = json.get("expires_in").getAsInt();
        
        msConfig.setAccessToken(getJsonString(json, "access_token"));
        msConfig.setRefreshToken(getJsonString(json, "refresh_token"));
        msConfig.setTokenType(getJsonString(json, "token_type"));
        msConfig.setScope(getJsonString(json, "scope"));
        msConfig.setAccessTokenExpiry(Instant.now().plusSeconds(expiresIn).getEpochSecond());
    }
}
