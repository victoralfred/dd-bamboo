package com.ddlabs.atlassian.metrics.model;

import junit.framework.TestCase;

public class SeriesBuilderTest extends TestCase {

    public void testBuildSeries() {
        // Arrange
        SeriesBuilder builder = new SeriesBuilder();
        String buildKey = "TEST-123";
        String buildNumber = "456";
        String result = "SUCCESS";
        BuildTag buildTag = new BuildTag(buildKey, buildNumber, result);
        String metric = "test.metric";
        String timestamp = "2023-10-01T12:00:00Z";
        String value = "100";
        String hostname = "test-host";
        MetricType metricType = MetricType.COUNTER; // Assuming COUNTER is a valid type
        // Act
        String series =
                builder.buildSeries(metric, timestamp, value, hostname,
                        metricType, buildTag);
        // Assert
        System.out.println(series.toString());
        assertNotNull(series);
//        assertTrue(series.contains("test.metric"));
//        assertTrue(series.contains("2023-10-01T12:00:00Z"));
//        assertTrue(series.contains("100"));
    }
}