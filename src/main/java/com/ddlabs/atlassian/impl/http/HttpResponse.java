package com.ddlabs.atlassian.impl.http;

import java.util.Map;

/**
 * Model class representing an HTTP response.
 */
public class HttpResponse {
    private final int statusCode;
    private final String body;
    private final Map<String, String> headers;
    
    public HttpResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getBody() {
        return body;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
}
