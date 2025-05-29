package com.ddlabs.atlassian.util;

import com.ddlabs.atlassian.exception.ValidationException;

import javax.annotation.CheckForNull;
import java.util.Collection;

/**
 * Utility class for validation operations.
 */
public final class ValidationUtils {
    
    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validates that the given object is not null.
     *
     * @param object The object to validate
     * @param message The error message if validation fails
     * @param <T> The type of the object
     * @return The validated object
     * @throws ValidationException If validation fails
     */
    public static <T> T validateNotNull(T object, String message) {
        if (object == null) {
            throw new ValidationException(message, message);
        }
        return object;
    }
    
    /**
     * Validates that the given string is not null or empty.
     *
     * @param string  The string to validate
     * @param message The error message if validation fails
     * @throws ValidationException If validation fails
     */
    public static void validateNotEmpty(String string, String message) {
        if (string == null || string.trim().isEmpty()) {
            throw new ValidationException(message, message);
        }
    }
    
    /**
     * Validates that the given collection is not null or empty.
     *
     * @param collection The collection to validate
     * @param message The error message if validation fails
     * @param <T> The type of the collection elements
     * @return The validated collection
     * @throws ValidationException If validation fails
     */
    public static <T> Collection<T> validateNotEmpty(Collection<T> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(message, message);
        }
        return collection;
    }
    
    /**
     * Validates that the given condition is true.
     *
     * @param condition The condition to validate
     * @param message The error message if validation fails
     * @throws ValidationException If validation fails
     */
    public static void validateCondition(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message, message);
        }
    }
    /**
     * Checks that the given reference is not null.
     *
     * @param reference The reference to check
     * @param <T> The type of the reference
     * @return The non-null reference
     * @throws NullPointerException If the reference is null
     */
    public static <T> T checkNotNull(@CheckForNull T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
