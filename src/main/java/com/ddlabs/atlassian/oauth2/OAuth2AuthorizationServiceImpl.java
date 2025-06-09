package com.ddlabs.atlassian.oauth2;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.api.OAuth2Service;
import com.ddlabs.atlassian.oauth2.model.GrantType;
import com.ddlabs.atlassian.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.impl.config.UserService;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.api.HttpClient;
import com.ddlabs.atlassian.api.MetricServer;
import com.ddlabs.atlassian.dao.adapter.entity.ConfigRepository;
import com.ddlabs.atlassian.impl.exception.AuthenticationException;
import com.ddlabs.atlassian.impl.exception.ErrorCode;
import com.ddlabs.atlassian.impl.exception.NullOrEmptyFieldsException;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;

@BambooComponent
public class OAuth2AuthorizationServiceImpl implements OAuth2Service {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthorizationServiceImpl.class);
    private final HttpClient httpClient;
    private final ConfigRepository configRepository;
    private final UserService userService;

    public OAuth2AuthorizationServiceImpl(HttpClient httpClient,
                                          ConfigRepository configRepository,
                                         UserService userService) {
        this.httpClient = httpClient;
        this.configRepository = configRepository;
        this.userService = userService;
    }
    @Override
    public String exchangeRefreshTokenForAccessToken(String grantType, 
                                                   String refreshToken, 
                                                   String clientKey,
                                                   String clientSecrete, 
                                                   String tokenEndpoint) {
        try {
            ValidationUtils.checkNotNullOrEmptyStrings(grantType, refreshToken, clientKey, clientSecrete, tokenEndpoint);
            String urlParameters = buildRefreshTokenUrl(grantType, refreshToken, clientKey, clientSecrete);
            return httpClient.post(
                    tokenEndpoint,
                    urlParameters,
                    "application/x-www-form-urlencoded"
            );
        } catch (NullOrEmptyFieldsException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void refreshToken(MetricServer metricServer) throws Exception {
        Assert.notNull(metricServer, "Server type cannot be null");
        String serverType = metricServer.getClass().getSimpleName();
         ServerConfigBuilder serverConfigBuilder =  configRepository.findByServerType(serverType);
        if (serverConfigBuilder==null) {
            log.warn("No configuration found for server: {}", serverType);
            return;
        }
        if (isAccessTokenExpired(serverConfigBuilder.getAccessTokenExpiry())) {
            refreshAndUpdateToken(serverConfigBuilder, serverType);
        } else {
            log.info("Access token for server {} is still valid.", serverType);
        }
    }
    @Override
    public String generateAuthorizationUrl(OAuth2Configuration config) throws AuthenticationException {
        try {
            ValidationUtils.validateNotNull(config, "OAuth2 configuration cannot be null");
            ValidationUtils.validateNotEmpty(config.getAuthEndpoint(), "Auth endpoint cannot be empty");
            ValidationUtils.validateNotEmpty(config.getClientId(), "Client ID cannot be empty");
            ValidationUtils.validateNotEmpty(config.getRedirectUri(), "Redirect URI cannot be empty");
            ValidationUtils.validateNotEmpty(config.getCodeChallenge(), "Code challenge cannot be empty");

            String codeChallengeMethod = config.getCodeChallengeMethod() != null ?
                    config.getCodeChallengeMethod() : "S256";

            return config.getAuthEndpoint() + "?" +
                    "redirect_uri=" + URLEncoder.encode(config.getRedirectUri(), StandardCharsets.UTF_8) +
                    "&client_id=" + URLEncoder.encode(config.getClientId(), StandardCharsets.UTF_8) +
                    "&response_type=code" +
                    "&code_challenge=" + URLEncoder.encode(config.getCodeChallenge(), StandardCharsets.UTF_8) +
                    "&code_challenge_method=" + codeChallengeMethod;
        } catch (Exception e) {
            LogUtils.logError(log, "Error generating authorization URL", e);
            throw new AuthenticationException(ErrorCode.AUTH_ERROR,
                    "Error generating authorization URL: " + e.getMessage(),
                    "Failed to generate authorization URL", e);
        }
    }
    @Override
    public OAuth2TokenResponse exchangeCodeForTokens(String code, OAuth2Configuration config) throws AuthenticationException {
        try {
            ValidationUtils.validateNotEmpty(code, "Authorization code cannot be empty");
            ValidationUtils.validateNotNull(config, "OAuth2 configuration cannot be null");
            ValidationUtils.validateNotEmpty(config.getTokenEndpoint(), "Token endpoint cannot be empty");
            ValidationUtils.validateNotEmpty(config.getClientId(), "Client ID cannot be empty");
            ValidationUtils.validateNotEmpty(config.getClientSecret(), "Client secret cannot be empty");
            ValidationUtils.validateNotEmpty(config.getRedirectUri(), "Redirect URI cannot be empty");
            ValidationUtils.validateNotEmpty(config.getCodeVerifier(), "Code verifier cannot be empty");

            String requestBody = "client_id=" + URLEncoder.encode(config.getClientId(), StandardCharsets.UTF_8) +
                    "&client_secret=" + URLEncoder.encode(config.getClientSecret(), StandardCharsets.UTF_8) +
                    "&redirect_uri=" + URLEncoder.encode(config.getRedirectUri(), StandardCharsets.UTF_8) +
                    "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                    "&code_verifier=" + URLEncoder.encode(config.getCodeVerifier(), StandardCharsets.UTF_8) +
                    "&grant_type=" + GrantType.AUTHORIZATION_CODE.getValue();

            String response = httpClient.post(config.getTokenEndpoint(), requestBody, "application/x-www-form-urlencoded");
            return parseTokenResponse(response);
        } catch (Exception e) {
            LogUtils.logError(log, "Error exchanging authorization code for tokens", e);
            throw new AuthenticationException(ErrorCode.TOKEN_EXCHANGE_FAILED,
                    "Error exchanging authorization code for tokens: " + e.getMessage(),
                    "Failed to exchange authorization code for tokens", e);
        }
    }
    @Override
    public boolean isAccessTokenExpired(long accessTokenExpiry) {
        return Instant.now().getEpochSecond() >= accessTokenExpiry;
    }
    @Override
    public boolean isTokenExpired(OAuth2TokenResponse tokenResponse) {
        ValidationUtils.validateNotNull(tokenResponse, "Token response cannot be null");
        return isAccessTokenExpired(tokenResponse.getAccessTokenExpiry());
    }
    private OAuth2TokenResponse parseTokenResponse(String response) {
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        String accessToken = getJsonString(json, "access_token");
        String refreshToken = getJsonString(json, "refresh_token");
        String tokenType = getJsonString(json, "token_type");
        String scope = getJsonString(json, "scope");
        long expiresIn = json.get("expires_in").getAsLong();
        return new OAuth2TokenResponse(accessToken, refreshToken, tokenType, scope, expiresIn);
    }
    private String getJsonString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return null;
        }
        return json.get(key).getAsString();
    }
    private void updateConfigWithNewTokens(ServerConfigBuilder msConfigEntity, String response) {
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        int expiresIn = json.get("expires_in").getAsInt();
        msConfigEntity.setAccessToken(ValidationUtils.getJsonString(json, "access_token"));
        msConfigEntity.setRefreshToken(ValidationUtils.getJsonString(json, "refresh_token"));
        msConfigEntity.setTokenType(ValidationUtils.getJsonString(json, "token_type"));
        msConfigEntity.setScope(ValidationUtils.getJsonString(json, "scope"));
        msConfigEntity.setAccessTokenExpiry(Instant.now().plusSeconds(expiresIn).getEpochSecond());
    }
    private void refreshAndUpdateToken(ServerConfigBuilder msConfigEntity, String serverType) throws Exception {
        try {
            String response = exchangeRefreshTokenForAccessToken(
                    GrantType.REFRESH_TOKEN.toString().toLowerCase(Locale.ROOT),
                    msConfigEntity.getRefreshToken(),
                    msConfigEntity.getClientId(),
                    userService.decrypt(msConfigEntity.getClientSecret()),
                    msConfigEntity.getTokenEndpoint()
            );
            updateConfigWithNewTokens(msConfigEntity, response);
            configRepository.update(msConfigEntity);
        } catch (Exception e) {
            log.error("Failed to refresh access token for server: {}", serverType, e);
            throw new Exception("Failed to refresh access token for server: " + serverType, e);
        }
    }
}
