package com.ddlabs.atlassian.dao.adapter;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.concurrent.atomic.AtomicReference;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.dao.adapter.entity.MSConfigEntity;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigMapper;
import com.ddlabs.atlassian.impl.exception.DataAccessException;
import com.ddlabs.atlassian.impl.exception.ErrorCode;

import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter implementation of PluginDaoRepository that delegates to ConfigRepository.
 * This class provides backward compatibility for code that depends on the old interface.
 */
@Component
public final class PluginDaoRepositoryAdapter {
    private final Logger log = LoggerFactory.getLogger(PluginDaoRepositoryAdapter.class);
    @ComponentImport
    private final ActiveObjects ao;
    private final ServerConfigMapper serverConfigMapper;
    public PluginDaoRepositoryAdapter( ActiveObjects ao, ServerConfigMapper serverConfigMapper) {
        this.ao = ValidationUtils.validateNotNull(ao, "ActiveObjects cannot be null");
        this.serverConfigMapper = serverConfigMapper;
    }

    /**
     * Saves a new server configuration to the database using the provided properties.
     * If a configuration already exists for the specified server type, no new configuration
     * will be created, and an appropriate message will be returned.
     *
     * @param properties an instance of {@code ServerConfigBuilder} containing
     *                   the details of the server configuration to save, such as client credentials,
     *                   redirect URL, server type, server name, and other related properties
     * @return a {@code String} indicating the result of the save operation, either "success",
     *         a message stating a configuration already exists for the specified type,
     *         or an error message in case of a failure during the save process
     */
    public String saveServerConfig(ServerConfigBuilder properties) {
        if(getServerConfigByType(properties.getServerType())!=null) {
            return "Server configuration already exists for type: " + properties.getServerType();
        }
       return ao.executeInTransaction(() -> {
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
               return "success";
           } catch (Exception e) {
               log.error("Error saving server configuration", e);
               return "Error saving server configuration: " + e.getMessage();
           } finally {
               ao.flushAll();
           }
       });
    }
    public ServerConfigBuilder getServerConfigByType(String serverType) {
        MSConfigEntity[] configs = ao.find(MSConfigEntity.class, "server_type = ?", serverType);
        return null != configs && configs.length > 0 ? serverConfigMapper.apply(configs[0]) : null;
    }

    /**
     * Updates the server configuration in the database with the provided configuration details.
     * This method updates the existing server configuration fields such as tokens, scope, tokens expiry,
     * and other related details based on the type of server configuration provided.
     * If no configuration is found for the specified server type, a warning is logged.
     *
     * @param <T> the type of the server configuration, which must extend {@code ServerConfigBuilder}
     * @param serverConfig the server configuration object containing updated values
     */
    public <T extends ServerConfigBuilder> void updateServerConfig(T serverConfig) {
        ao.executeInTransaction(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction() {
                MSConfigEntity[] config = ao.find(MSConfigEntity.class, "server_type = ?", serverConfig.getServerType());
                if (config.length == 0) {
                    log.warn("No server configuration found for type: {}", serverConfig.getServerType());
                    return null;
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
                return null;
            }
        });

    }

    /**
     * Retrieves a list of all server configurations present in the database.
     * This method fetches stored server configuration entities, maps them using the provided {@code ServerConfigMapper},
     * and returns a list of server configuration objects.
     * If no configurations are present, an empty list is returned.
     *
     * @return a {@code List<ServerConfigBuilder>} containing all server configuration objects;
     *         if no configurations exist, an empty list is returned.
     * @throws DataAccessException if there is an error while accessing or processing the data.
     */
    public List<ServerConfigBuilder> getAllServerConfigs() {
        AtomicReference<List<ServerConfigBuilder>> atomicReference = new AtomicReference<>();
       return Collections.singletonList(ao.executeInTransaction(() -> {
           try {
               List<MSConfigEntity> entities = List.of(ao.find(MSConfigEntity.class));
               List<ServerConfigBuilder> configs = new ArrayList<>();
               if (entities.isEmpty()) {
                   log.info("No server configurations found.");
                   atomicReference.set(configs);
               }
               configs = entities.stream()
                       .map(serverConfigMapper::apply)
                       .collect(Collectors.toList());
               atomicReference.set(configs);
           } catch (Exception e) {
               LogUtils.logError(log, "Error finding all server configs", e);
               throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR,
                       "Error finding all server configs",
                       "Failed to retrieve server configurations", e);
           }
           return atomicReference.get().isEmpty() ? null : atomicReference.get().get(0);
       }));

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
