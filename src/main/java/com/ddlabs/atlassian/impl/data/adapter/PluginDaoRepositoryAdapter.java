package com.ddlabs.atlassian.impl.data.adapter;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.data.adapter.entity.MSConfigEntity;
import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigMapper;
import com.ddlabs.atlassian.impl.exception.DataAccessException;
import com.ddlabs.atlassian.impl.exception.ErrorCode;
import com.ddlabs.atlassian.impl.http.TokenStore;

import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of PluginDaoRepository that delegates to ServerConfigRepository.
 * This class provides backward compatibility for code that depends on the old interface.
 */
@Component
@Transactional
public final class PluginDaoRepositoryAdapter {
    private final TokenStore tokenStore;
    private final Logger log = LoggerFactory.getLogger(PluginDaoRepositoryAdapter.class);
    @ComponentImport
    private final ActiveObjects ao;
    private final ServerConfigMapper serverConfigMapper;
    public PluginDaoRepositoryAdapter(TokenStore tokenStore, ActiveObjects ao, ServerConfigMapper serverConfigMapper) {
        this.tokenStore = tokenStore;
        this.ao = ValidationUtils.validateNotNull(ao, "ActiveObjects cannot be null");
        this.serverConfigMapper = serverConfigMapper;
    }
    public String saveServerConfig(ServerConfigBuilder properties) {
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
            serverConfig.setConfigured(properties.isConfigured());
            serverConfig.setEnabled(properties.isEnabled());
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
    public ServerConfigBuilder getServerConfigByType(String serverType) {
        Optional<MSConfigEntity[]> configs = Optional.ofNullable(
                ao.find(MSConfigEntity.class, "server_type = ?", serverType)
        );
        return configs.map(config -> config[0])
                .map(serverConfigMapper::apply)
                .orElseThrow(() -> new DataAccessException(ErrorCode.ENTITY_NOT_FOUND,
                        "Server configuration not found for type: " + serverType,
                        "No server configuration found for the specified type."));
    }

    public <T extends ServerConfigBuilder> void updateServerConfig(T serverConfig) {
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
        tokenStore.removeToken("datadog_access_token");
        tokenStore.putToken("datadog_access_token", serverConfig.getAccessToken());
    }
    public List<ServerConfigBuilder> getAllServerConfigs() {
        try {
        List<MSConfigEntity> entities = List.of(ao.find(MSConfigEntity.class));
        List<ServerConfigBuilder> configs = new ArrayList<>();
        if(entities.isEmpty()) {
            log.info("No server configurations found.");
            return configs;
        }
         return entities.stream()
                    .map(serverConfigMapper::apply)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LogUtils.logError(log, "Error finding all server configs", e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR,
                    "Error finding all server configs",
                    "Failed to retrieve server configurations", e);
        }
    }
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
}
