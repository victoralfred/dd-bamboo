package com.ddlabs.atlassian.impl.metrics.model;

import java.time.Instant;

/**
 * Represents a single point in a metric series.
 */
public class MetricPoint {
    private double value;
    private Instant timestamp;
    
    public MetricPoint() {
    }
    
    public MetricPoint(double value, Instant timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
