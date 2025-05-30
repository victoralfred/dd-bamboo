package com.ddlabs.atlassian.exception;

public class NullOrEmptyFieldsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NullOrEmptyFieldsException(String message) {
        super(message);
    }
    public NullOrEmptyFieldsException(Throwable cause) {
        super(cause);
    }
}
