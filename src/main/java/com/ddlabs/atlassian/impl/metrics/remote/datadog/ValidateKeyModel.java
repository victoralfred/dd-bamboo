package com.ddlabs.atlassian.impl.metrics.remote.datadog;

public class ValidateKeyModel {
    private final String API_KEY;
    private final String APP_KEY;
    private final String endpoint;

    public ValidateKeyModel(String apiKey, String appKey, String endpoint) {
        API_KEY = apiKey;
        APP_KEY = appKey;
        this.endpoint = endpoint;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public String getAPP_KEY() {
        return APP_KEY;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
