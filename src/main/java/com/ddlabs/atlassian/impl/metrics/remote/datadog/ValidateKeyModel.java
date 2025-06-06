package com.ddlabs.atlassian.impl.metrics.remote.datadog;

public class ValidateKeyModel {
    private String API_KEY;
    private String APP_KEY;
    private String endpoint;

    public ValidateKeyModel() {
    }

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

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public void setAPP_KEY(String APP_KEY) {
        this.APP_KEY = APP_KEY;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
