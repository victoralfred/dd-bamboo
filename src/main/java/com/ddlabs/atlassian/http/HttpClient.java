package com.ddlabs.atlassian.http;

import com.ddlabs.atlassian.exception.ApiException;

/**
 * Interface for HTTP client operations.
 */
public interface HttpClient {
    
    /**
     * Sends a GET request to the specified URL.
     *
     * @param url The URL to send the request to
     * @return The response body as a string
     * @throws ApiException If an error occurs
     */
    String get(String url) throws ApiException;
    
    /**
     * Sends a GET request to the specified URL with query parameters.
     *
     * @param url The URL to send the request to
     * @param queryParams The query parameters
     * @return The response body as a string
     * @throws ApiException If an error occurs
     */
    String get(String url, String queryParams) throws ApiException;
    
    /**
     * Sends a POST request to the specified URL with the specified body.
     *
     * @param url The URL to send the request to
     * @param body The request body
     * @return The response body as a string
     * @throws ApiException If an error occurs
     */
    String post(String url, String body) throws ApiException;
    
    /**
     * Sends a POST request to the specified URL with the specified body and content type.
     *
     * @param url The URL to send the request to
     * @param body The request body
     * @param contentType The content type of the request
     * @return The response body as a string
     * @throws ApiException If an error occurs
     */
    String post(String url, String body, String contentType) throws ApiException;
    
    /**
     * Sends a POST request to the specified URL with the specified object as the body.
     * The object will be serialized to JSON.
     *
     * @param url The URL to send the request to
     * @param body The request body as an object
     * @param <T> The type of the request body
     * @return The response body as a string
     * @throws ApiException If an error occurs
     */
    <T> String post(String url, T body) throws ApiException;
    /**
     * Sends a POST request to the specified URL with the specified series point, content type, and Datadog metric server.
     *
     * @param url The URL to send the request to
     * @param seriesPoint The series point to include in the request body
     * @param contentType The content type of the request
     * @param authToken The authentication token for the Datadog metric server
     */
    void post(String url, String seriesPoint, String contentType, String authToken) throws ApiException;
}
