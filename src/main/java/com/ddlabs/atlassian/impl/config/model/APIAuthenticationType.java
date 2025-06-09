package com.ddlabs.atlassian.impl.config.model;

public class APIAuthenticationType {
    private String apiKey;
    private String appKey;
    private String endpoint;
    public APIAuthenticationType() {
    }
    public APIAuthenticationType(String apiKey, String appKey, String endpoint) {
        this.apiKey = apiKey;
        this.appKey = appKey;
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
