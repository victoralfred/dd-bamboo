package com.ddlabs.atlassian.util;

import com.ddlabs.atlassian.util.exceptions.NullOrEmptyFieldsException;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperUtil {
    private static final Logger log = LoggerFactory.getLogger(HelperUtil.class);
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
