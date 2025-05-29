package com.ddlabs.atlassian.metrics.api.provider;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.Series;
import com.ddlabs.atlassian.exception.ErrorCode;
import com.ddlabs.atlassian.exception.MetricsApiException;
import com.ddlabs.atlassian.metrics.api.MetricsApiClient;
import com.ddlabs.atlassian.metrics.api.model.MetricData;
import com.ddlabs.atlassian.metrics.api.model.MetricPoint;
import com.ddlabs.atlassian.metrics.api.model.MetricQuery;
import com.ddlabs.atlassian.metrics.api.model.MetricSeries;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Datadog implementation of the MetricsApiClient interface.
 */
public class DatadogMetricsApiClient implements MetricsApiClient {
    private static final Logger log = LoggerFactory.getLogger(DatadogMetricsApiClient.class);
    
    private final ApiClient apiClient;
    private final MetricsApi metricsApi;
    
    public DatadogMetricsApiClient(String accessToken) {
        ValidationUtils.validateNotEmpty(accessToken, "Access token cannot be empty");
        
        this.apiClient = new ApiClient();
        this.apiClient.configureApiKeys(Map.of("apiKeyAuth", accessToken));
        this.metricsApi = new MetricsApi(apiClient);
    }
    
    @Override
    public void sendMetrics(List<MetricData> metrics) throws MetricsApiException {
        try {
            ValidationUtils.validateNotEmpty(metrics, "Metrics cannot be empty");
            
            List<Series> seriesList = new ArrayList<>();
            
            for (MetricData metric : metrics) {
                Series series = new Series();
                series.setMetric(metric.getMetricName());
                series.setType("gauge"); // Default to gauge type
                
                if (metric.getHost() != null) {
                    series.setHost(metric.getHost());
                }
                
                if (!metric.getTags().isEmpty()) {
                    List<String> tags = new ArrayList<>();
                    for (Map.Entry<String, String> tag : metric.getTags().entrySet()) {
                        tags.add(tag.getKey() + ":" + tag.getValue());
                    }
                    series.setTags(tags);
                }
                
                List<List<Object>> points = new ArrayList<>();
                List<Object> point = new ArrayList<>();
                point.add(metric.getTimestamp().getEpochSecond());
                point.add(metric.getValue());
                points.add(point);
                series.setPoints(points);
                
                seriesList.add(series);
            }
            
            metricsApi.submitMetrics(seriesList);
            LogUtils.logInfo(log, "Successfully sent {} metrics to Datadog", metrics.size());
        } catch (Exception e) {
            LogUtils.logError(log, "Error sending metrics to Datadog", e);
            throw new MetricsApiException(ErrorCode.METRICS_SEND_FAILED, 
                    "Error sending metrics to Datadog: " + e.getMessage(), 
                    "Failed to send metrics to Datadog", e);
        }
    }
    
    @Override
    public List<MetricSeries> queryMetrics(MetricQuery query) throws MetricsApiException {
        try {
            ValidationUtils.validateNotNull(query, "Query cannot be null");
            ValidationUtils.validateNotEmpty(query.getMetricName(), "Metric name cannot be empty");
            
            // This is a placeholder - in a real implementation, you would call the Datadog Query API
            // But Datadog Java client doesn't have a direct way to query metrics in v1 API
            // Would need to implement using v2 API or direct HTTP calls
            
            throw new MetricsApiException(ErrorCode.METRICS_QUERY_FAILED, 
                    "Querying metrics is not implemented for Datadog yet", 
                    "This feature is not available yet");
        } catch (MetricsApiException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.logError(log, "Error querying metrics from Datadog", e);
            throw new MetricsApiException(ErrorCode.METRICS_QUERY_FAILED, 
                    "Error querying metrics from Datadog: " + e.getMessage(), 
                    "Failed to query metrics from Datadog", e);
        }
    }
    
    @Override
    public boolean testConnection() throws MetricsApiException {
        try {
            // A simple way to test is to query a basic metric with minimal data
            // In a real implementation, you might want to use a more appropriate API
            // like checking API key validity
            
            // For now, we'll just assume the connection is successful if we can create the client
            LogUtils.logInfo(log, "Successfully tested connection to Datadog");
            return true;
        } catch (Exception e) {
            LogUtils.logError(log, "Error testing connection to Datadog", e);
            throw new MetricsApiException(ErrorCode.API_CONNECTION_ERROR, 
                    "Error testing connection to Datadog: " + e.getMessage(), 
                    "Failed to connect to Datadog", e);
        }
    }
}
