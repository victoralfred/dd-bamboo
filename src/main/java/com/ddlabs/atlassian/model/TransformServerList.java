package com.ddlabs.atlassian.model;

import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class TransformServerList implements Function<MSConfig, ConfiguredMetricServers> {

    @Override
    public ConfiguredMetricServers apply(MSConfig msConfig) {
        if (msConfig == null) {
            return null;
        }
        return new ConfiguredMetricServers(
                msConfig.getServerName(),
                msConfig.getConfigured(),
                msConfig.getEnabled(),
                msConfig.getServerType()// Assuming serverType is an integer where 1 indicates a specific type
        );
    }
}
