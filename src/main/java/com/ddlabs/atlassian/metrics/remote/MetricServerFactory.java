package com.ddlabs.atlassian.metrics.remote;


public interface MetricServerFactory {
    /**
     * Returns a MetricServer instance based on the provided provider.
     *
     * @param provider the provider for which to get the MetricServer
     * @return a MetricServer instance
     */
    MetricServer getMetricServer(String provider);
}
