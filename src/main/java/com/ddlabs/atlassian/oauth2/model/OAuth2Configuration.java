package com.ddlabs.atlassian.oauth2.model;

/**
 * Model class representing OAuth2 configuration.
 */
public class OAuth2Configuration {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authEndpoint;
    private String tokenEndpoint;
    private String apiEndpoint;
    private String codeVerifier;
    private String codeChallenge;
    private String codeChallengeMethod;
    
    public OAuth2Configuration() {
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
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    
    public String getAuthEndpoint() {
        return authEndpoint;
    }
    
    public void setAuthEndpoint(String authEndpoint) {
        this.authEndpoint = authEndpoint;
    }
    
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }
    
    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
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
    
    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }
    
    public void setCodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
    }

    @Override
    public String toString() {
        return "OAuth2Configuration{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", authEndpoint='" + authEndpoint + '\'' +
                ", tokenEndpoint='" + tokenEndpoint + '\'' +
                ", apiEndpoint='" + apiEndpoint + '\'' +
                ", codeVerifier='" + codeVerifier + '\'' +
                ", codeChallenge='" + codeChallenge + '\'' +
                ", codeChallengeMethod='" + codeChallengeMethod + '\'' +
                '}';
    }
}
