package com.ddlabs.atlassian.data.adapter;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.ddlabs.atlassian.api.PluginDaoRepository;

import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.exception.DataAccessException;
import com.ddlabs.atlassian.exception.ErrorCode;
import com.ddlabs.atlassian.metrics.model.*;

import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of PluginDaoRepository that delegates to ServerConfigRepository.
 * This class provides backward compatibility for code that depends on the old interface.
 */
@Component
public class PluginDaoRepositoryAdapter implements PluginDaoRepository {
    private final Logger log = LoggerFactory.getLogger(PluginDaoRepositoryAdapter.class);
    @ComponentImport
    private final ActiveObjects ao;
    private final TransformServerList transformServerList;
    public PluginDaoRepositoryAdapter(ActiveObjects ao, TransformServerList transformServerList) {
        this.ao = ValidationUtils.validateNotNull(ao, "ActiveObjects cannot be null");
        this.transformServerList = ValidationUtils.validateNotNull(transformServerList, "ServerConfigMapper cannot be null");

    }
    @Override
    public String saveServerConfig(ServerConfigDTO properties) {
        final MSConfigEntity serverConfig = ao.create(MSConfigEntity.class);
        try {
            serverConfig.setClientSecret(properties.getClientSecret());
            serverConfig.setClientId(properties.getClientId());
            serverConfig.setRedirectUrl(properties.getRedirectUrl());
            serverConfig.setCodeVerifier(properties.getCodeVerifier());
            serverConfig.setCodeChallenge(properties.getCodeChallenge());
            serverConfig.setServerType(properties.getServerType());
            serverConfig.setServerName(properties.getServerName());
            serverConfig.setDescription(properties.getDescription());
            serverConfig.setCodeExpirationTime(properties.getCodeExpirationTime());
            serverConfig.setCodeCreationTime(properties.getCodeCreationTime());
            serverConfig.setConfigured(false);
            serverConfig.setEnabled(false);
            serverConfig.setApiEndpoint(properties.getApiEndpoint());
            serverConfig.setOauthEndpoint(properties.getOauthEndpoint());
            serverConfig.setTokenEndpoint(properties.getTokenEndpoint());
            serverConfig.save();
            log.info("Server configuration saved for server: {}", properties.getServerName());
            return "Server configuration saved successfully.";
        } catch (Exception e) {
            log.error("Error saving server configuration", e);
            return "Error saving server configuration: " + e.getMessage();
        } finally {
            ao.flushAll();
        }
    }
    @Override
    public  MSConfigEntity getServerConfigByType(String serverType) {
        Optional<MSConfigEntity[]> configs = Optional.ofNullable(
                ao.find(MSConfigEntity.class, "server_type = ?", serverType)
        );
        return configs.map(config -> config[0]).orElse(null);
    }

    @Override
    public <T extends MSConfigEntity> void updateServerConfig(T serverConfig) {
        MSConfigEntity[] config = ao.find(MSConfigEntity.class, "server_type = ?", serverConfig.getServerType());
        if (config.length == 0) {
            log.warn("No server configuration found for type: {}", serverConfig.getServerType());
            return;
        }
        config[0].setAccessToken(serverConfig.getAccessToken());
        config[0].setRefreshToken(serverConfig.getRefreshToken());
        config[0].setTokenType(serverConfig.getTokenType());
        config[0].setScope(serverConfig.getScope());
        config[0].setAccessTokenExpiry(serverConfig.getAccessTokenExpiry());
        config[0].setConfigured(true);
        config[0].setEnabled(true);
        config[0].setSite(serverConfig.getSite());
        config[0].setDomain(serverConfig.getDomain());
        config[0].setOrgId(serverConfig.getOrgId());
        config[0].setOrgName(serverConfig.getOrgName());
        config[0].save();

    }
    @Override
    public List<ConfiguredMetricServers> getAllServerConfigs() {
        try {
        List<MSConfigEntity> entities = List.of(ao.find(MSConfigEntity.class));
        List<ConfiguredMetricServers> configs = new ArrayList<>();
        if(entities.isEmpty()) {
            log.info("No server configurations found.");
            return configs;
        }
         return entities.stream()
                    .map(transformServerList)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LogUtils.logError(log, "Error finding all server configs", e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR,
                    "Error finding all server configs",
                    "Failed to retrieve server configurations", e);
        }
    }


    @Override
    public void deleteServerConfig(String serverType) {
        ao.executeInTransaction(() -> {
            MSConfigEntity[] configs = ao.find(MSConfigEntity.class, "server_type = ?", serverType);
            if (configs.length > 0) {
                ao.delete(configs[0]);
                log.info("Server configuration deleted for type: {}", serverType);
            } else {
                log.warn("No server configuration found: {}", serverType);
            }
            return null;
        });
    }

    @Override
    public List<MSConfigEntity> getMsConfig() {
        return List.of(ao.find(MSConfigEntity.class));
    }
}
