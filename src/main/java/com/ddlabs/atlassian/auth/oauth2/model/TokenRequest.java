package com.ddlabs.atlassian.auth.oauth2.model;

/**
 * Model class representing an OAuth2 token request.
 */
public class TokenRequest {
    private final GrantType grantType;
    private final String code;
    private final String redirectUri;
    private final String clientId;
    private final String clientSecret;
    private final String codeVerifier;
    private final String refreshToken;
    
    private TokenRequest(Builder builder) {
        this.grantType = builder.grantType;
        this.code = builder.code;
        this.redirectUri = builder.redirectUri;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.codeVerifier = builder.codeVerifier;
        this.refreshToken = builder.refreshToken;
    }
    
    public GrantType getGrantType() {
        return grantType;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getCodeVerifier() {
        return codeVerifier;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private GrantType grantType;
        private String code;
        private String redirectUri;
        private String clientId;
        private String clientSecret;
        private String codeVerifier;
        private String refreshToken;
        
        public Builder grantType(GrantType grantType) {
            this.grantType = grantType;
            return this;
        }
        
        public Builder code(String code) {
            this.code = code;
            return this;
        }
        
        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }
        
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        
        public Builder codeVerifier(String codeVerifier) {
            this.codeVerifier = codeVerifier;
            return this;
        }
        
        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public TokenRequest build() {
            return new TokenRequest(this);
        }
    }
}
