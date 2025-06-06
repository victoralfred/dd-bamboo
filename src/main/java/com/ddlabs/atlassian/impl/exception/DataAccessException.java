package com.ddlabs.atlassian.impl.exception;

/**
 * Exception thrown when a data access error occurs.
 */
public class DataAccessException extends BambooMetricsException {
    
    public DataAccessException(ErrorCode errorCode, String developerMessage, String userMessage) {
        super(errorCode, developerMessage, userMessage);
    }
    
    public DataAccessException(ErrorCode errorCode, String developerMessage, String userMessage, Throwable cause) {
        super(errorCode, developerMessage, userMessage, cause);
    }
}
