package com.ddlabs.atlassian.impl.config.model;

public class ConfigDefaults {
    private final String pluginRedirectUrl;
    private final String tokenEndpoint;
    private final String authorizationEndpoint;
    private final String apiEndpoint;
    public ConfigDefaults(String apiEndpoint, String authorizationEndpoint, String tokenEndpoint, String pluginRedirectUrl) {
        this.apiEndpoint = apiEndpoint;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.pluginRedirectUrl = pluginRedirectUrl;
    }
    public String getPluginRedirectUrl() {
        return pluginRedirectUrl;
    }
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }
    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }
    public String getApiEndpoint() {
        return apiEndpoint;
    }
}
