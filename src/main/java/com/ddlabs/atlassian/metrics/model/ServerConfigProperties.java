package com.ddlabs.atlassian.metrics.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ServerConfigProperties {
    private String serverName;
    private String serverUrl;
    private String description;
    private String serverType;
    private String redirectUrl;
    private String codeVerifier;
    private String codeChallenge;
    private Long codeCreationTime;
    private Long codeExpirationTime;
    private String clientId;
    private String clientSecret;
    private String apiEndpoint;
    private String oauthEndpoint;
    private String tokenEndpoint;
    public ServerConfigProperties() {
    }

    public ServerConfigProperties(String tokenEndpoint, String oauthEndpoint, String apiEndpoint, String clientSecret, String clientId, Long codeExpirationTime, Long codeCreationTime, String codeChallenge, String codeVerifier, String redirectUrl, String serverType, String description, String serverName) {
        this.tokenEndpoint = tokenEndpoint;
        this.oauthEndpoint = oauthEndpoint;
        this.apiEndpoint = apiEndpoint;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
        this.codeExpirationTime = codeExpirationTime;
        this.codeCreationTime = codeCreationTime;
        this.codeChallenge = codeChallenge;
        this.codeVerifier = codeVerifier;
        this.redirectUrl = redirectUrl;
        this.serverType = serverType;
        this.description = description;
        this.serverName = serverName;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getOauthEndpoint() {
        return oauthEndpoint;
    }

    public void setOauthEndpoint(String oauthEndpoint) {
        this.oauthEndpoint = oauthEndpoint;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getCodeExpirationTime() {
        return codeExpirationTime;
    }

    public void setCodeExpirationTime(Long codeExpirationTime) {
        this.codeExpirationTime = codeExpirationTime;
    }

    public Long getCodeCreationTime() {
        return codeCreationTime;
    }
    public void setCodeCreationTime(Long codeCreationTime) {
        this.codeCreationTime = codeCreationTime;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ServerConfigProperties that = (ServerConfigProperties) o;
        return new EqualsBuilder().append(getServerName(), that.getServerName()).append(getServerUrl(), that.getServerUrl()).append(getDescription(), that.getDescription()).append(getServerType(), that.getServerType()).append(getRedirectUrl(), that.getRedirectUrl()).append(getCodeVerifier(), that.getCodeVerifier()).append(getCodeChallenge(), that.getCodeChallenge()).append(getCodeCreationTime(), that.getCodeCreationTime()).append(getCodeExpirationTime(), that.getCodeExpirationTime()).append(getClientId(), that.getClientId()).append(getClientSecret(), that.getClientSecret()).append(getApiEndpoint(), that.getApiEndpoint()).append(getOauthEndpoint(), that.getOauthEndpoint()).append(getTokenEndpoint(), that.getTokenEndpoint()).isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getServerName()).append(getServerUrl()).append(getDescription()).append(getServerType()).append(getRedirectUrl()).append(getCodeVerifier()).append(getCodeChallenge()).append(getCodeCreationTime()).append(getCodeExpirationTime()).append(getClientId()).append(getClientSecret()).append(getApiEndpoint()).append(getOauthEndpoint()).append(getTokenEndpoint()).toHashCode();
    }
}