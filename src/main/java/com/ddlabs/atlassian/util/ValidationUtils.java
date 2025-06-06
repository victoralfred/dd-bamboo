package com.ddlabs.atlassian.util;

import com.ddlabs.atlassian.impl.exception.NullOrEmptyFieldsException;
import com.ddlabs.atlassian.impl.exception.ValidationException;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import java.util.Collection;

/**
 * Utility class for validation operations.
 */
public final class ValidationUtils {
    private static final Logger log = LoggerFactory.getLogger(ValidationUtils.class);

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
    public static void checkNotNullOrEmptyStrings(String... values) {
        if (values == null) {
            throw new NullOrEmptyFieldsException(new NullPointerException("String array is null"));
        }
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value == null || value.trim().isEmpty()) {
                log.warn("String argument at index {} is null or empty", i);
                throw new NullOrEmptyFieldsException(
                        new IllegalArgumentException("String value at index " + i + " is null or empty"));
            }
        }
    }
    public static void checkNotNull(Object... values) {
        if (values == null) {
            throw new NullOrEmptyFieldsException(new NullPointerException("Object array is null"));
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                log.warn("Object argument at index {} is null", i);
                throw new NullOrEmptyFieldsException(
                        new NullPointerException("Object value at index " + i + " is null"));
            }
        }
    }
    public static String getJsonString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            throw new IllegalArgumentException("Missing or null key in JSON: " + key);
        }
        return json.get(key).getAsString();
    }
}
