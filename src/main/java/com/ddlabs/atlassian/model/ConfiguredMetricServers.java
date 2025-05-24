package com.ddlabs.atlassian.model;

import java.io.Serializable;

public class ConfiguredMetricServers implements Serializable {
    private static final long serialVersionUID = 89800000L;
    private String name;
    private boolean online;
    private boolean authentication;
    private String serverType;

    public ConfiguredMetricServers() {
    }

    public ConfiguredMetricServers(String name, boolean online, boolean authentication, String serverType) {
        this.name = name;
        this.online = online;
        this.authentication = authentication;
        this.serverType = serverType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    public String getServerType() {
        return serverType;
    }
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
}
