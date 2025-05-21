package com.ddlabs.atlassian.model;

public class ServerConfigurationFields {
    private String serverName;
    private String serverUrl;
    private String description;

    public ServerConfigurationFields() {
    }

    public ServerConfigurationFields(String serverName, String serverUrl, String description) {
        this.serverName = serverName;
        this.serverUrl = serverUrl;
        this.description = description;
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
}