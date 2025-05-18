package com.ddlabs.atlassian.model;

public class ConfiguredMetricServers {
    private String name;
    private boolean online;
    private boolean authentication;

    public ConfiguredMetricServers() {
    }

    public ConfiguredMetricServers(String name, boolean online, boolean authentication) {
        this.name = name;
        this.online = online;
        this.authentication = authentication;
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
}
