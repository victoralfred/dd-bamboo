package com.ddlabs.atlassian.impl.metrics.model;

public enum MetricType {
    COUNTER(0), GAUGE(1), RATE(2), HISTOGRAM(3), DISTRIBUTION(4);
    private final int value;
    MetricType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public static MetricType fromValue(int value) {
        for (MetricType type : MetricType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MetricType value: " + value);
    }
}
