package com.ddlabs.atlassian.model;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.CheckForNull;

@Component
public class PluginDaoRepositoryImpl implements PluginDaoRepository {
    @ComponentImport
    private final ActiveObjects ao;
    public PluginDaoRepositoryImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }
    @Override
    public String saveServerConfig(ServerConfigurationFields serverConfigFields) {
        final MSConfig serverConfig = ao.create(MSConfig.class);
        try {
            serverConfig.getClientSecret("clientSecret");
            serverConfig.getClientId("clientId");
            serverConfig.getDomain("domain");
            serverConfig.getSite("site");
            serverConfig.getOrgName("orgName");
            serverConfig.getOrgId("orgId");
            serverConfig.getBambooRestApiUrl("redirectUri");
            serverConfig.save();
            return "Server configuration saved successfully.";
        } catch (Exception e) {
            return "Error saving server configuration: " + e.getMessage();
        } finally {
            ao.flushAll();
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
