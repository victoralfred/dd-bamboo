package com.ddlabs.atlassian.util;

import org.slf4j.Logger;

/**
 * Utility class for logging operations.
 */
public final class LogUtils {
    
    private LogUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Logs an error message with exception details.
     *
     * @param logger The logger to use
     * @param message The error message
     * @param e The exception
     */
    public static void logError(Logger logger, String message, Throwable e) {
        logger.error("{}: {}. Exception: {}", message, e.getMessage(), e.getClass().getName(), e);
    }
    
    /**
     * Logs a warning message.
     *
     * @param logger The logger to use
     * @param message The warning message
     * @param args Message arguments
     */
    public static void logWarning(Logger logger, String message, Object... args) {
        logger.warn(message, args);
    }
    
    /**
     * Logs an info message.
     *
     * @param logger The logger to use
     * @param message The info message
     * @param args Message arguments
     */
    public static void logInfo(Logger logger, String message, Object... args) {
        logger.info(message, args);
    }
    
    /**
     * Logs a debug message.
     *
     * @param logger The logger to use
     * @param message The debug message
     * @param args Message arguments
     */
    public static void logDebug(Logger logger, String message, Object... args) {
        logger.debug(message, args);
    }
}
