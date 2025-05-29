package com.ddlabs.atlassian.data.adapter;

import com.atlassian.activeobjects.tx.Transactional;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.repository.ServerConfigRepository;
import com.ddlabs.atlassian.exception.DataAccessException;
import com.ddlabs.atlassian.metrics.model.ConfiguredMetricServers;
import com.ddlabs.atlassian.metrics.model.MSConfig;
import com.ddlabs.atlassian.metrics.model.ServerConfigProperties;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter implementation of PluginDaoRepository that delegates to ServerConfigRepository.
 * This class provides backward compatibility for code that depends on the old interface.
 */
@Component
@Transactional
public class PluginDaoRepositoryAdapter implements PluginDaoRepository {
    private static final Logger log = LoggerFactory.getLogger(PluginDaoRepositoryAdapter.class);
    
    private final ServerConfigRepository serverConfigRepository;
    
    public PluginDaoRepositoryAdapter(ServerConfigRepository serverConfigRepository) {
        this.serverConfigRepository = serverConfigRepository;
    }
    
    @Override
    public String saveServerConfig(ServerConfigProperties properties) {
        try {
            ServerConfigDTO configDTO = new ServerConfigDTO();
            configDTO.setServerType(properties.getServerType());
            configDTO.setServerName(properties.getServerName());
            configDTO.setDescription(properties.getDescription());
            configDTO.setClientId(properties.getClientId());
            configDTO.setClientSecret(properties.getClientSecret());
            configDTO.setRedirectUrl(properties.getRedirectUrl());
            configDTO.setCodeVerifier(properties.getCodeVerifier());
            configDTO.setCodeChallenge(properties.getCodeChallenge());
            configDTO.setCodeCreationTime(properties.getCodeCreationTime());
            configDTO.setCodeExpirationTime(properties.getCodeExpirationTime());
            configDTO.setConfigured(false);
            configDTO.setEnabled(false);
            configDTO.setApiEndpoint(properties.getApiEndpoint());
            configDTO.setOauthEndpoint(properties.getOauthEndpoint());
            configDTO.setTokenEndpoint(properties.getTokenEndpoint());
            
            serverConfigRepository.save(configDTO);
            
            return "Server configuration saved successfully.";
        } catch (DataAccessException e) {
            LogUtils.logError(log, "Error saving server configuration", e);
            return "Error saving server configuration: " + e.getMessage();
        } catch (Exception e) {
            LogUtils.logError(log, "Unexpected error saving server configuration", e);
            return "Unexpected error saving server configuration: " + e.getMessage();
        }
    }
    
    @Override
    public MSConfig getServerConfigByType(String serverType) {
        try {
            ServerConfigDTO configDTO = serverConfigRepository.findByServerType(serverType);
            if (configDTO == null) {
                return null;
            }
            
            // This is a temporary solution - in a complete refactoring, we would create a proper adapter for MSConfig
            MSConfig config = new MSConfig() {
                @Override
                public Integer getID() {
                    return null; // We don't have the ID in the DTO
                }
                
                @Override
                public String getServerType() {
                    return configDTO.getServerType();
                }
                
                @Override
                public void setServerType(String serverType) {
                    configDTO.setServerType(serverType);
                }
                
                @Override
                public String getServerName() {
                    return configDTO.getServerName();
                }
                
                @Override
                public void setServerName(String serverName) {
                    configDTO.setServerName(serverName);
                }
                
                @Override
                public String getDescription() {
                    return configDTO.getDescription();
                }
                
                @Override
                public void setDescription(String description) {
                    configDTO.setDescription(description);
                }
                
                @Override
                public String getClientId() {
                    return configDTO.getClientId();
                }
                
                @Override
                public void setClientId(String clientId) {
                    configDTO.setClientId(clientId);
                }
                
                @Override
                public String getClientSecret() {
                    return configDTO.getClientSecret();
                }
                
                @Override
                public void setClientSecret(String clientSecret) {
                    configDTO.setClientSecret(clientSecret);
                }
                
                @Override
                public String getRedirectUrl() {
                    return configDTO.getRedirectUrl();
                }
                
                @Override
                public void setRedirectUrl(String redirectUrl) {
                    configDTO.setRedirectUrl(redirectUrl);
                }
                
                @Override
                public String getCodeVerifier() {
                    return configDTO.getCodeVerifier();
                }
                
                @Override
                public void setCodeVerifier(String codeVerifier) {
                    configDTO.setCodeVerifier(codeVerifier);
                }
                
                @Override
                public String getCodeChallenge() {
                    return configDTO.getCodeChallenge();
                }
                
                @Override
                public void setCodeChallenge(String codeChallenge) {
                    configDTO.setCodeChallenge(codeChallenge);
                }
                
                @Override
                public long getCodeCreationTime() {
                    return configDTO.getCodeCreationTime();
                }
                
                @Override
                public void setCodeCreationTime(long codeCreationTime) {
                    configDTO.setCodeCreationTime(codeCreationTime);
                }
                
                @Override
                public long getCodeExpirationTime() {
                    return configDTO.getCodeExpirationTime();
                }
                
                @Override
                public void setCodeExpirationTime(long codeExpirationTime) {
                    configDTO.setCodeExpirationTime(codeExpirationTime);
                }
                
                @Override
                public String getAccessToken() {
                    return configDTO.getAccessToken();
                }
                
                @Override
                public void setAccessToken(String accessToken) {
                    configDTO.setAccessToken(accessToken);
                }
                
                @Override
                public String getRefreshToken() {
                    return configDTO.getRefreshToken();
                }
                
                @Override
                public void setRefreshToken(String refreshToken) {
                    configDTO.setRefreshToken(refreshToken);
                }
                
                @Override
                public String getTokenType() {
                    return configDTO.getTokenType();
                }
                
                @Override
                public void setTokenType(String tokenType) {
                    configDTO.setTokenType(tokenType);
                }
                
                @Override
                public String getScope() {
                    return configDTO.getScope();
                }
                
                @Override
                public void setScope(String scope) {
                    configDTO.setScope(scope);
                }
                
                @Override
                public long getAccessTokenExpiry() {
                    return configDTO.getAccessTokenExpiry();
                }
                
                @Override
                public void setAccessTokenExpiry(long accessTokenExpiry) {
                    configDTO.setAccessTokenExpiry(accessTokenExpiry);
                }
                
                @Override
                public boolean isConfigured() {
                    return configDTO.isConfigured();
                }
                
                @Override
                public void setConfigured(boolean configured) {
                    configDTO.setConfigured(configured);
                }
                
                @Override
                public boolean isEnabled() {
                    return configDTO.isEnabled();
                }
                
                @Override
                public void setEnabled(boolean enabled) {
                    configDTO.setEnabled(enabled);
                }
                
                @Override
                public String getApiEndpoint() {
                    return configDTO.getApiEndpoint();
                }
                
                @Override
                public void setApiEndpoint(String apiEndpoint) {
                    configDTO.setApiEndpoint(apiEndpoint);
                }
                
                @Override
                public String getOauthEndpoint() {
                    return configDTO.getOauthEndpoint();
                }
                
                @Override
                public void setOauthEndpoint(String oauthEndpoint) {
                    configDTO.setOauthEndpoint(oauthEndpoint);
                }
                
                @Override
                public String getTokenEndpoint() {
                    return configDTO.getTokenEndpoint();
                }
                
                @Override
                public void setTokenEndpoint(String tokenEndpoint) {
                    configDTO.setTokenEndpoint(tokenEndpoint);
                }
                
                @Override
                public String getSite() {
                    return configDTO.getSite();
                }
                
                @Override
                public void setSite(String site) {
                    configDTO.setSite(site);
                }
                
                @Override
                public String getDomain() {
                    return configDTO.getDomain();
                }
                
                @Override
                public void setDomain(String domain) {
                    configDTO.setDomain(domain);
                }
                
                @Override
                public String getOrgId() {
                    return configDTO.getOrgId();
                }
                
                @Override
                public void setOrgId(String orgId) {
                    configDTO.setOrgId(orgId);
                }
                
                @Override
                public String getOrgName() {
                    return configDTO.getOrgName();
                }
                
                @Override
                public void setOrgName(String orgName) {
                    configDTO.setOrgName(orgName);
                }
                
                @Override
                public void save() {
                    // This is not needed as we're using the adapter
                }
            };
            
            return config;
        } catch (Exception e) {
            LogUtils.logError(log, "Error getting server configuration by type: " + serverType, e);
            return null;
        }
    }
    
    @Override
    public <T extends MSConfig> void updateServerConfig(T serverConfig) {
        try {
            ServerConfigDTO configDTO = new ServerConfigDTO();
            configDTO.setServerType(serverConfig.getServerType());
            configDTO.setServerName(serverConfig.getServerName());
            configDTO.setDescription(serverConfig.getDescription());
            configDTO.setClientId(serverConfig.getClientId());
            configDTO.setClientSecret(serverConfig.getClientSecret());
            configDTO.setRedirectUrl(serverConfig.getRedirectUrl());
            configDTO.setCodeVerifier(serverConfig.getCodeVerifier());
            configDTO.setCodeChallenge(serverConfig.getCodeChallenge());
            configDTO.setCodeCreationTime(serverConfig.getCodeCreationTime());
            configDTO.setCodeExpirationTime(serverConfig.getCodeExpirationTime());
            configDTO.setAccessToken(serverConfig.getAccessToken());
            configDTO.setRefreshToken(serverConfig.getRefreshToken());
            configDTO.setTokenType(serverConfig.getTokenType());
            configDTO.setScope(serverConfig.getScope());
            configDTO.setAccessTokenExpiry(serverConfig.getAccessTokenExpiry());
            configDTO.setConfigured(serverConfig.isConfigured());
            configDTO.setEnabled(serverConfig.isEnabled());
            configDTO.setApiEndpoint(serverConfig.getApiEndpoint());
            configDTO.setOauthEndpoint(serverConfig.getOauthEndpoint());
            configDTO.setTokenEndpoint(serverConfig.getTokenEndpoint());
            configDTO.setSite(serverConfig.getSite());
            configDTO.setDomain(serverConfig.getDomain());
            configDTO.setOrgId(serverConfig.getOrgId());
            configDTO.setOrgName(serverConfig.getOrgName());
            
            serverConfigRepository.update(configDTO);
        } catch (Exception e) {
            LogUtils.logError(log, "Error updating server configuration", e);
            throw new RuntimeException("Error updating server configuration: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ConfiguredMetricServers> getAllServerConfigs() {
        try {
            List<ServerConfigDTO> configs = serverConfigRepository.findAll();
            List<ConfiguredMetricServers> result = new ArrayList<>(configs.size());
            
            for (ServerConfigDTO configDTO : configs) {
                ConfiguredMetricServers metricServer = new ConfiguredMetricServers();
                metricServer.setServerType(configDTO.getServerType());
                metricServer.setServerName(configDTO.getServerName());
                metricServer.setDescription(configDTO.getDescription());
                metricServer.setConfigured(configDTO.isConfigured());
                metricServer.setEnabled(configDTO.isEnabled());
                
                result.add(metricServer);
            }
            
            return result;
        } catch (Exception e) {
            LogUtils.logError(log, "Error getting all server configurations", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void deleteServerConfig(String serverType) {
        try {
            serverConfigRepository.delete(serverType);
        } catch (Exception e) {
            LogUtils.logError(log, "Error deleting server configuration: " + serverType, e);
            throw new RuntimeException("Error deleting server configuration: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<MSConfig> getMsConfig() {
        try {
            List<ServerConfigDTO> configs = serverConfigRepository.findAll();
            List<MSConfig> result = new ArrayList<>(configs.size());
            
            for (ServerConfigDTO configDTO : configs) {
                result.add(getServerConfigByType(configDTO.getServerType()));
            }
            
            return result;
        } catch (Exception e) {
            LogUtils.logError(log, "Error getting MS configurations", e);
            return new ArrayList<>();
        }
    }
}