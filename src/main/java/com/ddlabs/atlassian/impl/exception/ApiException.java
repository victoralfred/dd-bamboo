package com.ddlabs.atlassian.impl.exception;

/**
 * Exception thrown when an API error occurs.
 */
public class ApiException extends BambooMetricsException {
    
    public ApiException(ErrorCode errorCode, String developerMessage, String userMessage) {
        super(errorCode, developerMessage, userMessage);
    }
    
    public ApiException(ErrorCode errorCode, String developerMessage, String userMessage, Throwable cause) {
        super(errorCode, developerMessage, userMessage, cause);
    }
}
