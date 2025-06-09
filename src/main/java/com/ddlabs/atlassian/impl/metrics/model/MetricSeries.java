package com.ddlabs.atlassian.impl.metrics.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a series of metric data points.
 */
public class MetricSeries {
    private String metricName;
    private Map<String, String> tags;
    private List<MetricPoint> points;
    
    public MetricSeries() {
        this.tags = new HashMap<>();
        this.points = new ArrayList<>();
    }
    
    public MetricSeries(String metricName) {
        this();
        this.metricName = metricName;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
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
    
    public List<MetricPoint> getPoints() {
        return points;
    }
    
    public void setPoints(List<MetricPoint> points) {
        this.points = points != null ? points : new ArrayList<>();
    }
    
    public void addPoint(MetricPoint point) {
        this.points.add(point);
    }
}
