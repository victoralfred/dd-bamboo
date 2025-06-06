package com.ddlabs.atlassian.impl.exception;

/**
 * Exception thrown when an authentication error occurs.
 */
public class AuthenticationException extends BambooMetricsException {
    
    public AuthenticationException(ErrorCode errorCode, String developerMessage, String userMessage) {
        super(errorCode, developerMessage, userMessage);
    }
    
    public AuthenticationException(ErrorCode errorCode, String developerMessage, String userMessage, Throwable cause) {
        super(errorCode, developerMessage, userMessage, cause);
    }
}
