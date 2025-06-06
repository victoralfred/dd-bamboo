package com.ddlabs.atlassian.impl.http;

import com.ddlabs.atlassian.api.HttpClient;
import com.ddlabs.atlassian.impl.exception.ApiException;
import com.ddlabs.atlassian.impl.exception.ErrorCode;
import com.ddlabs.atlassian.impl.metrics.remote.datadog.ValidateKeyModel;
import com.ddlabs.atlassian.util.LogUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the HTTP client interface.
 */
@Component
public class DefaultHttpClient implements HttpClient {
    private static final Logger log = LoggerFactory.getLogger(DefaultHttpClient.class);
    private static final int DEFAULT_TIMEOUT_MS = 30000; // 30 seconds
    private final Gson gson;

    public DefaultHttpClient() {
        this.gson = new Gson();
    }
    
    @Override
    public String get(String url) throws ApiException {
        return get(url, null);
    }
    @Override
    public String get(ValidateKeyModel validateKeyModel) throws ApiException {
        String fullUrl = validateKeyModel.getEndpoint() + "/api/v1/validate";
        try {
            HttpsURLConnection connection = openConnection(fullUrl, "GET");
            connection.setRequestProperty("DD-API-KEY",  validateKeyModel.getApiKey());
            connection.setRequestProperty("DD-APP-KEY", validateKeyModel.getAppKey());
            String response = handleResponse(connection);
            LogUtils.logInfo(log, "Api key validated {} succeeded with response: {}", fullUrl, response);
            return response;
        } catch (IOException | URISyntaxException e) {
            LogUtils.logError(log, "Error sending GET request to " + fullUrl, e);
            throw new ApiException(ErrorCode.API_CONNECTION_ERROR,
                    "Error sending GET request to " + fullUrl,
                    "Failed to connect to the API", e);
        }
    }
    @Override
    public String get(String url, String queryParams) throws ApiException {
        try {
            String fullUrl = queryParams != null && !queryParams.isEmpty()
                    ? url + (url.contains("?") ? "&" : "?") + queryParams
                    : url;

            HttpsURLConnection connection = openConnection(fullUrl, "GET");
            return handleResponse(connection);
        } catch (IOException | URISyntaxException e) {
            LogUtils.logError(log, "Error sending GET request to " + url, e);
            throw new ApiException(ErrorCode.API_CONNECTION_ERROR,
                    "Error sending GET request to " + url,
                    "Failed to connect to the API", e);
        }
    }
    
    @Override
    public String post(String url, String body) throws ApiException {
        return post(url, body, "application/json");
    }
    
    @Override
    public String post(String url, String body, String contentType) throws ApiException {
        try {
            HttpsURLConnection connection = openConnection(url, "POST");
            connection.setRequestProperty("Content-Type", contentType);
            
            if (body != null && !body.isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            return handleResponse(connection);
        } catch (IOException | URISyntaxException e) {
            LogUtils.logError(log, "Error sending POST request to " + url, e);
            throw new ApiException(ErrorCode.API_CONNECTION_ERROR, 
                    "Error sending POST request to " + url, 
                    "Failed to connect to the API", e);
        }
    }
    
    @Override
    public <T> String post(String url, T body) throws ApiException {
        String jsonBody = gson.toJson(body);
        return post(url, jsonBody);
    }

    @Override
    public void post(String url, String seriesPoint, String contentType, String authToken) throws ApiException {
        try{
            HttpsURLConnection connection = openConnection(url, "POST");
            //@todo: Do not commit this hardcoded API keys to the repository
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("DD-API-KEY",  "");
            connection.setRequestProperty("DD-APP-KEY", "");


            if (seriesPoint != null && !seriesPoint.isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = seriesPoint.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            String response = handleResponse(connection);
            LogUtils.logInfo(log, "POST request to {} succeeded with response: {}", url, response);
        } catch (IOException | URISyntaxException e) {
            LogUtils.logError(log, "Error sending POST request to " + url, e);
            throw new ApiException(ErrorCode.API_CONNECTION_ERROR,
                    "Error sending POST request to " + url,
                    "Failed to connect to the API", e);
        }
    }

    private HttpsURLConnection openConnection(String urlString, String method) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(DEFAULT_TIMEOUT_MS);
        connection.setReadTimeout(DEFAULT_TIMEOUT_MS);
        connection.setInstanceFollowRedirects(true);
        return connection;
    }
    
    private String handleResponse(HttpsURLConnection connection) throws IOException, ApiException {
        int statusCode = connection.getResponseCode();
        if (statusCode >= 200 && statusCode < 300) {
            return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            String errorBody = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            LogUtils.logWarning(log, "HTTP request failed with status code {}: {}", statusCode, errorBody);
            ErrorCode errorCode;
            String userMessage;
            switch (statusCode) {
                case 400:
                    errorCode = ErrorCode.API_ERROR;
                    userMessage = "Invalid request";
                    break;
                case 401:
                    errorCode = ErrorCode.AUTH_ERROR;
                    userMessage = "Authentication failed";
                    break;
                case 403:
                    errorCode = ErrorCode.AUTH_ERROR;
                    userMessage = "Not authorized to access this resource";
                    break;
                case 404:
                    errorCode = ErrorCode.API_ERROR;
                    userMessage = "Resource not found";
                    break;
                case 429:
                    errorCode = ErrorCode.API_ERROR;
                    userMessage = "Too many requests";
                    break;
                case 500:
                case 502:
                case 503:
                case 504:
                    errorCode = ErrorCode.API_ERROR;
                    userMessage = "Server error";
                    break;
                default:
                    errorCode = ErrorCode.API_ERROR;
                    userMessage = "Unexpected error";
            }

            throw new ApiException(errorCode,
                    "HTTP request failed with status code " + statusCode + ": " + errorBody, 
                    userMessage);
        }
    }
    
    private Map<String, String> extractHeaders(HttpsURLConnection connection) {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            if (entry.getKey() != null && !entry.getValue().isEmpty()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return headers;
    }
}
