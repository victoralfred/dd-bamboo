package com.ddlabs.atlassian.metrics.model;

import java.util.List;
import java.util.stream.Collectors;

public class SeriesData implements Data {
    private final List<Series> series;
    public SeriesData(List<Series> series) {
        this.series = series;
    }
    public List<Series> getSeries() {
        return series;
    }
    @Override
    public String toString() {
        return "{\n" +
                "\"series\": ["+ series.stream()
                .map(Series::toString)
                .collect(Collectors.joining(", ")) +
                "]\n"+
                "}";
    }
}
