package com.ddlabs.atlassian.impl.config.model;

import java.io.Serial;
import java.io.Serializable;

public class AvailableServers implements Serializable {
    @Serial
    private static final long serialVersionUID = 89800000L;
    private boolean isAuthenticated;
    private String serverType;
    private String serverName;
    private boolean enabled;
    private String description;

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public AvailableServers(String serverName, boolean enabled, boolean isAuthenticated,
                            String serverType, String description) {
        this.serverName = serverName;
        this.enabled = enabled;
        this.isAuthenticated = isAuthenticated;
        this.serverType = serverType;
        this.description = description;
    }
}