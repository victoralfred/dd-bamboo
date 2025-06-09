package com.ddlabs.atlassian.dao.adapter.dto;

import com.ddlabs.atlassian.impl.config.model.AvailableServers;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.Function;
@Component
public class AvailableServerDTO implements Function<ServerConfigBuilder, AvailableServers> {

    @Override
    public AvailableServers apply(ServerConfigBuilder configDTO) {
        if (configDTO == null) {
            return null;
        }
        return new AvailableServers(
                configDTO.getServerName(),
                configDTO.isEnabled(),
                configDTO.getAccessToken() != null
                        && configDTO.getAccessTokenExpiry()> Instant.now().getEpochSecond(),
                configDTO.getServerType(),
                configDTO.getDescription()
        );
    }
}
