package com.ddlabs.atlassian.impl.metrics.remote;

import com.ddlabs.atlassian.api.MetricServer;
import com.ddlabs.atlassian.api.MetricServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MetricServerFactoryImpl implements MetricServerFactory {
    private final Logger logger = LoggerFactory.getLogger(MetricServerFactoryImpl.class);
    private final Map<String, MetricServer> metricServers;
    public MetricServerFactoryImpl(Map<String, MetricServer> metricServers) {
        this.metricServers = new HashMap<>(metricServers);
        for (MetricServer metricServer : metricServers.values()) {
            this.metricServers.put(metricServer.getClass().getSimpleName().toLowerCase(), metricServer);
        }
    }
    @Override
    public MetricServer getMetricServer(String provider) {
        metricServers.forEach((k, v) -> logger.debug("Registered MetricServer: {} -> {}", k, v.getClass().getSimpleName()));
        return Optional.ofNullable(metricServers.get(provider.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown metric provider: " + provider));
    }
}
