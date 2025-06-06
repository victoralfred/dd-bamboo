package com.ddlabs.atlassian.impl.metrics.api.factory;

import com.ddlabs.atlassian.impl.exception.MetricsApiException;
import com.ddlabs.atlassian.api.MetricsApiClient;
import com.ddlabs.atlassian.impl.metrics.api.model.MetricData;
import com.ddlabs.atlassian.impl.metrics.api.model.MetricQuery;
import com.ddlabs.atlassian.impl.metrics.api.model.MetricSeries;

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
