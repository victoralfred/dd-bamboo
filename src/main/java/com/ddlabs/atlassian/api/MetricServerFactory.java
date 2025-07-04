package com.ddlabs.atlassian.api;


public interface MetricServerFactory {
    /**
     * Returns a MetricServer instance based on the provided provider.
     *
     * @param provider the provider for which to get the MetricServer
     * @return a MetricServer instance
     */
    MetricServer getMetricServer(String provider);
}
