package com.ddlabs.atlassian.auth.oauth2.service;

import com.ddlabs.atlassian.auth.oauth2.model.GrantType;
import com.ddlabs.atlassian.auth.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.auth.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.exception.AuthenticationException;
import com.ddlabs.atlassian.exception.ErrorCode;
import com.ddlabs.atlassian.http.HttpClient;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Default implementation of the OAuth2 service interface.
 */
@Component
public class DefaultOAuth2Service implements OAuth2Service {
    private static final Logger log = LoggerFactory.getLogger(DefaultOAuth2Service.class);
    
    private final HttpClient httpClient;

    public DefaultOAuth2Service(HttpClient httpClient) {
        this.httpClient = httpClient;
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
    public OAuth2TokenResponse refreshAccessToken(String refreshToken, OAuth2Configuration config) throws AuthenticationException {
        return null;
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
}
