package com.ddlabs.atlassian.impl.exception;

/**
 * Base exception class for all exceptions in the Bamboo Metrics application.
 * Provides standardized error handling with error codes and user-friendly messages.
 */
public class BambooMetricsException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String userMessage;
    
    public BambooMetricsException(ErrorCode errorCode, String developerMessage, String userMessage) {
        super(developerMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public BambooMetricsException(ErrorCode errorCode, String developerMessage, String userMessage, Throwable cause) {
        super(developerMessage, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
}
