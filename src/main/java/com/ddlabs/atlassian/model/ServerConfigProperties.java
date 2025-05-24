package com.ddlabs.atlassian.model;

import java.time.Instant;

public class ServerConfigProperties {
    private String serverName;
    private String serverUrl;
    private String description;
    private String serverType;
    private String redirectUrl;
    private String codeVerifier;
    private String codeChallenge;
    private Instant codeCreationTime;
    private Instant codeExpirationTime;
    public ServerConfigProperties() {
    }
    public ServerConfigProperties(String serverName, String serverUrl, String description, String serverType,
                                  String redirectUrl, String codeVerifier, String codeChallenge, Instant codeCreationTime,
                                  Instant codeExpirationTime) {
        this.serverName = serverName;
        this.serverUrl = serverUrl;
        this.description = description;
        this.serverType = serverType;
        this.redirectUrl = redirectUrl;
        this.codeVerifier = codeVerifier;
        this.codeChallenge = codeChallenge;
        this.codeCreationTime = codeCreationTime;
        this.codeExpirationTime = codeExpirationTime;
    }
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public String getServerUrl() {
        return serverUrl;
    }
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getServerType() {
        return serverType;
    }
    public void setServerType(String serverType) {
        this.serverType = serverType;
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
    public Instant getCodeCreationTime() {
        return codeCreationTime;
    }
    public void setCodeCreationTime(Instant codeCreationTime) {
        this.codeCreationTime = codeCreationTime;
    }
    public Instant getCodeExpirationTime() {
        return codeExpirationTime;
    }
    public void setCodeExpirationTime(Instant codeExpirationTime) {
        this.codeExpirationTime = codeExpirationTime;
    }
}