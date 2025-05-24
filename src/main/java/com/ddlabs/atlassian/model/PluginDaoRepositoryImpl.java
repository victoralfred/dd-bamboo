package com.ddlabs.atlassian.model;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.CheckForNull;
import java.util.List;

@Component
public class PluginDaoRepositoryImpl implements PluginDaoRepository {
    @ComponentImport
    private final ActiveObjects ao;
    public PluginDaoRepositoryImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }
    @Override
    public String saveServerConfig(ServerConfigProperties serverConfigFields) {
        final MSConfig serverConfig = ao.create(MSConfig.class);
        try {
            serverConfig.setClientSecret(serverConfig.getClientSecret());
            serverConfig.setClientId(serverConfig.getClientId());
            serverConfig.setDomain(serverConfig.getDomain());
            serverConfig.setSite(serverConfig.getDomain());
            serverConfig.setOrgName(serverConfig.getOrgName());
            serverConfig.setOrgId(serverConfig.getOrgId());
            serverConfig.setRedirectUrl(serverConfig.getRedirectUrl());
            serverConfig.setCodeVerifier(serverConfigFields.getCodeVerifier());
            serverConfig.setCodeChallenge(serverConfig.getCodeChallenge());
            serverConfig.setServerType(serverConfig.getServerType());
            serverConfig.setServerName(serverConfig.getServerName());
            serverConfig.setDescription(serverConfig.getDescription());
            serverConfig.save();
            return "Server configuration saved successfully.";
        } catch (Exception e) {
            return "Error saving server configuration: " + e.getMessage();
        } finally {
            ao.flushAll();
        }
    }
    @Override
    public MSConfig getServerConfigByName(String serverName) {
        MSConfig[] configs = ao.find(MSConfig.class, "serverName = ?", serverName);
        if (configs.length == 0) {
            return null;
        }
        return configs[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends MSConfig> void updateServerConfig(T serverConfig) {
        ao.migrate(MSConfig.class);
        for (MSConfig config : ao.find(MSConfig.class, "serverName = ?", serverConfig.getServerName())) {
            config.save();
        }
    }

    public static <T> T checkNotNull(@CheckForNull T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
