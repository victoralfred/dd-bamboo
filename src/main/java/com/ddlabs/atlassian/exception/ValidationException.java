package com.ddlabs.atlassian.exception;

/**
 * Exception thrown when a validation error occurs.
 */
public class ValidationException extends BambooMetricsException {
    
    public ValidationException(String developerMessage, String userMessage) {
        super(ErrorCode.VALIDATION_ERROR, developerMessage, userMessage);
    }
    
    public ValidationException(String developerMessage, String userMessage, Throwable cause) {
        super(ErrorCode.VALIDATION_ERROR, developerMessage, userMessage, cause);
    }
}
