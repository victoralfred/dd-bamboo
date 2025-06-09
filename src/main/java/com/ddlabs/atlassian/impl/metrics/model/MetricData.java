package com.ddlabs.atlassian.impl.metrics.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single metric data point.
 */
public class MetricData {
    private String metricName;
    private double value;
    private Instant timestamp;
    private Map<String, String> tags;
    private String host;
    
    public MetricData() {
        this.tags = new HashMap<>();
        this.timestamp = Instant.now();
    }
    
    public MetricData(String metricName, double value) {
        this();
        this.metricName = metricName;
        this.value = value;
    }
    
    public MetricData(String metricName, double value, Instant timestamp) {
        this(metricName, value);
        this.timestamp = timestamp;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
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
    
    public Map<String, String> getTags() {
        return tags;
    }
    
    public void setTags(Map<String, String> tags) {
        this.tags = tags != null ? tags : new HashMap<>();
    }
    
    public void addTag(String key, String value) {
        this.tags.put(key, value);
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
}
