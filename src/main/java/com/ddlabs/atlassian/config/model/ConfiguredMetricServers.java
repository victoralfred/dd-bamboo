package com.ddlabs.atlassian.config.model;

import java.io.Serial;
import java.io.Serializable;

public class ConfiguredMetricServers implements Serializable {
    @Serial
    private static final long serialVersionUID = 89800000L;
    private boolean online;
    private boolean authentication;
    private String serverType;
    private String serverName;
    private boolean configured;
    private boolean enabled;
    private String description;

    public ConfiguredMetricServers(String serverName, boolean online, boolean authentication, String serverType,  boolean configured, boolean enabled, String description) {
        this.online = online;
        this.authentication = authentication;
        this.serverType = serverType;
        this.serverName = serverName;
        this.configured = configured;
        this.enabled = enabled;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
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

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}