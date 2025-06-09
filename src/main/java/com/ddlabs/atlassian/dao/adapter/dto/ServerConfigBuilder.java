package com.ddlabs.atlassian.dao.adapter.dto;

/**
 * Data Transfer Object for server configuration.
 */
public class ServerConfigBuilder {
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
    
    public ServerConfigBuilder() {
    }
    public ServerConfigBuilder(ServerBuilder builder) {
        this.serverType = builder.serverType;
        this.serverName = builder.serverName;
        this.description = builder.description;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.redirectUrl = builder.redirectUrl;
        this.codeVerifier = builder.codeVerifier;
        this.codeChallenge = builder.codeChallenge;
        this.codeCreationTime = builder.codeCreationTime;
        this.codeExpirationTime = builder.codeExpirationTime;
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.tokenType = builder.tokenType;
        this.scope = builder.scope;
        this.accessTokenExpiry = builder.accessTokenExpiry;
        this.configured = builder.configured;
        this.enabled = builder.enabled;
        this.apiEndpoint = builder.apiEndpoint;
        this.oauthEndpoint = builder.oauthEndpoint;
        this.tokenEndpoint = builder.tokenEndpoint;
        this.site = builder.site;
        this.domain = builder.domain;
        this.orgId = builder.orgId;
        this.orgName = builder.orgName;
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

    /**
     * Check if Object is with the Or clausw
     * @return true/false
     */
    public boolean nonNull(){
        return this.serverName != null || this.serverType != null || this.description != null || this.clientId != null
                || this.clientSecret != null || this.redirectUrl != null || this.codeVerifier != null
                || this.codeChallenge != null || this.codeCreationTime != 0 || this.codeExpirationTime != 0
                || this.accessToken != null ||this.refreshToken != null || this.tokenType != null || this.scope != null
                || this.accessTokenExpiry != 0 || this.configured || this.enabled
                || this.apiEndpoint != null || this.oauthEndpoint != null ||this.tokenEndpoint != null
                || this.site != null || this.domain != null || this.orgId != null ||this.orgName != null;
    }

    public final static class ServerBuilder{
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
        // Builder methods for each field
        public ServerBuilder serverType(String serverType) {
            this.serverType = serverType;
            return this;
        }
        public ServerBuilder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }
        public ServerBuilder description(String description) {
            this.description = description;
            return this;
        }
        public ServerBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        public ServerBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        public ServerBuilder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }
        public ServerBuilder codeVerifier(String codeVerifier) {
            this.codeVerifier = codeVerifier;
            return this;
        }
        public ServerBuilder codeChallenge(String codeChallenge) {
            this.codeChallenge = codeChallenge;
            return this;
        }
        public ServerBuilder codeCreationTime(long codeCreationTime) {
            this.codeCreationTime = codeCreationTime;
            return this;
        }
        public ServerBuilder codeExpirationTime(long codeExpirationTime) {
            this.codeExpirationTime = codeExpirationTime;
            return this;
        }
        public ServerBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        public ServerBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        public ServerBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        public ServerBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }
        public ServerBuilder accessTokenExpiry(long accessTokenExpiry) {
            this.accessTokenExpiry = accessTokenExpiry;
            return this;
        }
        public ServerBuilder configured(boolean configured) {
            this.configured = configured;
            return this;
        }
        public ServerBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        public ServerBuilder apiEndpoint(String apiEndpoint) {
            this.apiEndpoint = apiEndpoint;
            return this;
        }
        public ServerBuilder oauthEndpoint(String oauthEndpoint) {
            this.oauthEndpoint = oauthEndpoint;
            return this;
        }
        public ServerBuilder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }
        public ServerBuilder site(String site) {
            this.site = site;
            return this;
        }
        public ServerBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }
        public ServerBuilder orgId(String orgId) {
            this.orgId = orgId;
            return this;
        }
        public ServerBuilder orgName(String orgName) {
            this.orgName = orgName;
            return this;
        }
        public ServerConfigBuilder build() {
            return new ServerConfigBuilder(this);
        }
    }
}
