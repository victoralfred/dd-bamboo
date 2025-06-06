package com.ddlabs.atlassian.oauth2.model;

import java.time.Instant;

/**
 * Model class representing an OAuth2 token response.
 */
public class OAuth2TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String scope;
    private long expiresIn;
    private long accessTokenExpiry;
    
    public OAuth2TokenResponse() {
    }
    
    public OAuth2TokenResponse(String accessToken, String refreshToken, String tokenType, String scope, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expiresIn = expiresIn;
        this.accessTokenExpiry = Instant.now().plusSeconds(expiresIn).getEpochSecond();
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        this.accessTokenExpiry = Instant.now().plusSeconds(expiresIn).getEpochSecond();
    }
    
    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
    
    public void setAccessTokenExpiry(long accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }
}
