package com.ddlabs.atlassian.metrics.api.factory;

import com.ddlabs.atlassian.exception.MetricsApiException;
import com.ddlabs.atlassian.metrics.api.MetricsApiClient;
import com.ddlabs.atlassian.metrics.api.model.MetricData;
import com.ddlabs.atlassian.metrics.api.model.MetricQuery;
import com.ddlabs.atlassian.metrics.api.model.MetricSeries;

import java.util.List;

public class DatadogMetricsApiClient implements MetricsApiClient {
    public DatadogMetricsApiClient(String accessToken) {
    }

    @Override
    public void sendMetrics(List<MetricData> metrics) throws MetricsApiException {

    }

    @Override
    public List<MetricSeries> queryMetrics(MetricQuery query) throws MetricsApiException {
        return List.of();
    }

    @Override
    public boolean testConnection() throws MetricsApiException {
        return false;
    }
}
