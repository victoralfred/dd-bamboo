package com.ddlabs.atlassian.exception;

/**
 * Error codes for the Bamboo Metrics application.
 * Each error code has a unique identifier and a default message.
 */
public enum ErrorCode {
    // Generic errors
    UNKNOWN_ERROR(1000, "An unknown error occurred"),
    VALIDATION_ERROR(1001, "Invalid input data"),
    CONFIGURATION_ERROR(1002, "Configuration error"),
    
    // Authentication errors
    AUTH_ERROR(2000, "Authentication error"),
    TOKEN_EXCHANGE_FAILED(2001, "Failed to exchange authorization code for tokens"),
    TOKEN_REFRESH_FAILED(2002, "Failed to refresh access token"),
    TOKEN_EXPIRED(2003, "Access token has expired"),
    INVALID_CONFIGURATION(2004, "Invalid OAuth2 configuration"),
    
    // Data access errors
    DATA_ACCESS_ERROR(3000, "Data access error"),
    ENTITY_NOT_FOUND(3001, "Entity not found"),
    
    // API errors
    API_ERROR(4000, "API error"),
    API_CONNECTION_ERROR(4001, "Failed to connect to external API"),
    API_TIMEOUT(4002, "API request timed out"),
    
    // Metrics errors
    METRICS_ERROR(5000, "Metrics error"),
    METRICS_SEND_FAILED(5001, "Failed to send metrics"),
    METRICS_QUERY_FAILED(5002, "Failed to query metrics");
    
    private final int code;
    private final String defaultMessage;
    
    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
