package com.ddlabs.atlassian.data.dto;

/**
 * Data Transfer Object for server configuration.
 */
public class ServerConfigDTO {
    private String serverType;
    private String serverName;
    private String description;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String codeVerifier;
    private String codeChallenge;
    private long codeCreationTime;
    private long codeExpirationTime;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String scope;
    private long accessTokenExpiry;
    private boolean configured;
    private boolean enabled;
    private String apiEndpoint;
    private String oauthEndpoint;
    private String tokenEndpoint;
    private String site;
    private String domain;
    private String orgId;
    private String orgName;
    
    public ServerConfigDTO() {
    }
    
    // Getters and setters
    public String getServerType() {
        return serverType;
    }
    
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public String getCodeVerifier() {
        return codeVerifier;
    }
    
    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
    
    public String getCodeChallenge() {
        return codeChallenge;
    }
    
    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }
    
    public long getCodeCreationTime() {
        return codeCreationTime;
    }
    
    public void setCodeCreationTime(long codeCreationTime) {
        this.codeCreationTime = codeCreationTime;
    }
    
    public long getCodeExpirationTime() {
        return codeExpirationTime;
    }
    
    public void setCodeExpirationTime(long codeExpirationTime) {
        this.codeExpirationTime = codeExpirationTime;
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
    
    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
    
    public void setAccessTokenExpiry(long accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }
    
    public boolean isConfigured() {
        return configured;
    }
    
    public void setConfigured(boolean configured) {
        this.configured = configured;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
    
    public String getOauthEndpoint() {
        return oauthEndpoint;
    }
    
    public void setOauthEndpoint(String oauthEndpoint) {
        this.oauthEndpoint = oauthEndpoint;
    }
    
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }
    
    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }
    
    public String getSite() {
        return site;
    }
    
    public void setSite(String site) {
        this.site = site;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getOrgId() {
        return orgId;
    }
    
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    
    public String getOrgName() {
        return orgName;
    }
    
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
