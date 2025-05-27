package com.ddlabs.atlassian.model;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.CheckForNull;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PluginDaoRepositoryImpl implements PluginDaoRepository {
    private final Logger log = LoggerFactory.getLogger(PluginDaoRepositoryImpl.class);

    @ComponentImport
    private final ActiveObjects ao;
    private final TransformServerList transformServerList;
    public PluginDaoRepositoryImpl(ActiveObjects ao, TransformServerList transformServerList) {
        this.ao = checkNotNull(ao);
        this.transformServerList = transformServerList;
    }
    @Override
    public String saveServerConfig(ServerConfigProperties properties) {
        final MSConfig serverConfig = ao.create(MSConfig.class);
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
    public MSConfig getServerConfigByType(String serverType) {
        MSConfig[] configs = ao.find(MSConfig.class, "server_type = ?", serverType);
        if (configs.length == 0) {
            return null;
        }
        return configs[0];
    }

    @Override
    public <T extends MSConfig> void updateServerConfig(T serverConfig) {
        MSConfig[] config = ao.find(MSConfig.class, "server_type = ?", serverConfig.getServerType());
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
        List<MSConfig> configs = List.of(ao.find(MSConfig.class));
        if (configs.isEmpty()) {
            log.info("No server configurations found.");
            return List.of();
        }
        return configs.stream().map(transformServerList).collect(Collectors.toList());
    }

    @Override
    public void deleteServerConfig(String serverType) {
        ao.executeInTransaction(() -> {
            MSConfig[] configs = ao.find(MSConfig.class, "server_type = ?", serverType);
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
    public List<MSConfig> getMsConfig() {
        return List.of(ao.find(MSConfig.class));
    }

    public static <T> T checkNotNull(@CheckForNull T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
