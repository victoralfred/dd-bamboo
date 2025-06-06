package com.ddlabs.atlassian.impl.exception;

/**
 * Exception thrown when a metrics API error occurs.
 */
public class MetricsApiException extends BambooMetricsException {
    
    public MetricsApiException(ErrorCode errorCode, String developerMessage, String userMessage) {
        super(errorCode, developerMessage, userMessage);
    }
    
    public MetricsApiException(ErrorCode errorCode, String developerMessage, String userMessage, Throwable cause) {
        super(errorCode, developerMessage, userMessage, cause);
    }
}
