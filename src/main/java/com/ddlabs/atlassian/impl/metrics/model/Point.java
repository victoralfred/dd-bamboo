package com.ddlabs.atlassian.impl.metrics.model;

public class Point implements Data{
    private final String timestamp;
    private final String value;
    public Point(String timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"timestamp\": \"" + escapeJson(timestamp) + "\",\n" +
                "  \"value\": \"" + escapeJson(value) + "\"\n" +
                "}";
    }
}
