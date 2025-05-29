package com.ddlabs.atlassian.metrics.api;

import com.ddlabs.atlassian.exception.MetricsApiException;
import com.ddlabs.atlassian.metrics.api.model.MetricData;
import com.ddlabs.atlassian.metrics.api.model.MetricQuery;
import com.ddlabs.atlassian.metrics.api.model.MetricSeries;

import java.util.List;

/**
 * Interface for interacting with metrics APIs.
 */
public interface MetricsApiClient {
    
    /**
     * Sends metrics to the metrics service.
     *
     * @param metrics The metrics to send
     * @throws MetricsApiException If an error occurs
     */
    void sendMetrics(List<MetricData> metrics) throws MetricsApiException;
    
    /**
     * Queries metrics from the metrics service.
     *
     * @param query The query parameters
     * @return The metric series
     * @throws MetricsApiException If an error occurs
     */
    List<MetricSeries> queryMetrics(MetricQuery query) throws MetricsApiException;
    
    /**
     * Tests the connection to the metrics service.
     *
     * @return True if the connection is successful, false otherwise
     * @throws MetricsApiException If an error occurs
     */
    boolean testConnection() throws MetricsApiException;
}
