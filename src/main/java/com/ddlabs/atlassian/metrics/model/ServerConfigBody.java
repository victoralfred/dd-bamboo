package com.ddlabs.atlassian.metrics.model;

public class ServerConfigBody {
    private String apiEndpoint;
    private String clientKey;
    private String clientSecret;
    private String description;
    private String oauthEndpoint;
    private String redirectUrl;
    private String serverName;
    private String serverType;
    private String tokenEndpoint;
    public ServerConfigBody() {}
    // Getters and setters for each field

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOauthEndpoint() {
        return oauthEndpoint;
    }

    public void setOauthEndpoint(String oauthEndpoint) {
        this.oauthEndpoint = oauthEndpoint;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public String toString() {
        return "ServerConfigBody{" +
                "apiEndpoint='" + apiEndpoint + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", description='" + description + '\'' +
                ", oauthEndpoint='" + oauthEndpoint + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverType='" + serverType + '\'' +
                ", tokenEndpoint='" + tokenEndpoint + '\'' +
                '}';
    }
}
