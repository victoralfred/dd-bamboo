package com.ddlabs.atlassian.impl.metrics.remote.datadog;

public class ValidateKeyModel {
    private String apiKey;
    private String appKey;
    private String endpoint;
    public ValidateKeyModel() {
    }
    public ValidateKeyModel(String apiKey, String appKey, String endpoint) {
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
