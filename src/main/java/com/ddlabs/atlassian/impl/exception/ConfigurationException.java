package com.ddlabs.atlassian.impl.exception;

/**
 * Exception thrown when a configuration error occurs.
 */
public class ConfigurationException extends BambooMetricsException {
    
    public ConfigurationException(ErrorCode errorCode, String developerMessage, String userMessage) {
        super(errorCode, developerMessage, userMessage);
    }
    
    public ConfigurationException(ErrorCode errorCode, String developerMessage, String userMessage, Throwable cause) {
        super(errorCode, developerMessage, userMessage, cause);
    }
}
