package com.ddlabs.atlassian.metrics.model;

import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.Function;
@Component
public class TransformServerList implements Function<MSConfigEntity, ConfiguredMetricServers> {

    @Override
    public ConfiguredMetricServers apply(MSConfigEntity msConfigEntity) {
        if (msConfigEntity == null) {
            return null;
        }
        return new ConfiguredMetricServers(
                msConfigEntity.getServerName(),
                msConfigEntity.getEnabled(),
                msConfigEntity.getAccessToken() != null && msConfigEntity.getAccessTokenExpiry()> Instant.now().getEpochSecond(),
                msConfigEntity.getServerType(),
                msConfigEntity.getConfigured(),
                msConfigEntity.getEnabled(),
                msConfigEntity.getDescription()
        );
    }
}
