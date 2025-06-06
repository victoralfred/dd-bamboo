package com.ddlabs.atlassian.impl.metrics.model;

import java.util.List;
import java.util.stream.Collectors;

public class Series implements Data {
    private final String metrics;
    private final int metricType;
    private final List<Point> points;
    private final List<Resource> resources;
    private final List<String> tags;
    public Series(String metrics, MetricType metricType, List<Point> points, List<Resource> resources, List<String> tags) {
        this.metrics = metrics;
        this.metricType = metricType.getValue();
        this.points = points;
        this.resources = resources;
        this.tags = tags;
    }
    public String getMetrics() {
        return metrics;
    }
    public int getMetricType() {
        return metricType;
    }
    public List<Point> getPoints() {
        return points;
    }
    public List<Resource> getResources() {
        return resources;
    }
    public List<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        String pointsJson = points.stream()
                .map(Point::toString)
                .collect(Collectors.joining(", "));
        String resourcesJson = resources.stream()
                .map(Resource::toString)
                .collect(Collectors.joining(", "));
        String tagsJson = tags.stream()
                .map(tag -> "\"" + escapeJson(tag) + "\"")
                .collect(Collectors.joining(", "));
        return "{\n" +
                "  \"metric\": \"" + escapeJson(metrics) + "\",\n" +
                "  \"type\": " + metricType + ",\n" +
                "  \"points\": [" + pointsJson + "],\n" +
                "  \"resources\": [" + resourcesJson + "],\n" +
                "  \"tags\": [" + tagsJson + "]\n" +
                "}";
    }
}
