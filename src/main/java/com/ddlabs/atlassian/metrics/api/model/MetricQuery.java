package com.ddlabs.atlassian.metrics.api.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a query for metrics.
 */
public class MetricQuery {
    private String metricName;
    private Instant from;
    private Instant to;
    private Map<String, String> tags;
    private int limit;
    
    public MetricQuery() {
        this.tags = new HashMap<>();
        this.to = Instant.now();
        this.from = this.to.minusSeconds(3600); // Default to last hour
        this.limit = 1000;
    }
    
    public MetricQuery(String metricName) {
        this();
        this.metricName = metricName;
    }
    
    public MetricQuery(String metricName, Instant from, Instant to) {
        this(metricName);
        this.from = from;
        this.to = to;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
    
    public Instant getFrom() {
        return from;
    }
    
    public void setFrom(Instant from) {
        this.from = from;
    }
    
    public Instant getTo() {
        return to;
    }
    
    public void setTo(Instant to) {
        this.to = to;
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
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
