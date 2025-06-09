package com.ddlabs.atlassian.impl.metrics.factory;

import com.ddlabs.atlassian.api.OAuth2Service;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigMapper;
import com.ddlabs.atlassian.dao.adapter.entity.ConfigRepository;
import com.ddlabs.atlassian.impl.exception.ConfigurationException;
import com.ddlabs.atlassian.impl.exception.ErrorCode;
import com.ddlabs.atlassian.api.MetricsApiClient;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Factory for creating MetricsApiClient instances.
 */
@Component
public class MetricsApiClientFactory {
    private static final Logger log = LoggerFactory.getLogger(MetricsApiClientFactory.class);
    
    private final ConfigRepository configRepository;
    private final OAuth2Service oauth2Service;
    private final ServerConfigMapper serverConfigMapper;
    public MetricsApiClientFactory(ConfigRepository configRepository, OAuth2Service oauth2Service, ServerConfigMapper serverConfigMapper) {
        this.configRepository = ValidationUtils.validateNotNull(configRepository,
                "ConfigRepository cannot be null");
        this.oauth2Service = ValidationUtils.validateNotNull(oauth2Service, 
                "OAuth2Service cannot be null");
        this.serverConfigMapper = serverConfigMapper;
    }
    
    /**
     * Creates a MetricsApiClient for the specified server type.
     *
     * @param serverType The server type
     * @return The MetricsApiClient
     * @throws ConfigurationException If an error occurs
     */
    public MetricsApiClient createClient(String serverType) throws ConfigurationException {
        try {
            ValidationUtils.validateNotEmpty(serverType, "Server type cannot be empty");
            
            ServerConfigBuilder config = serverConfigMapper.toDtoForAccessTokenCreation(
                    configRepository.findByServerType(serverType));
            if (config == null) {
                throw new ConfigurationException(ErrorCode.CONFIGURATION_ERROR, 
                        "No configuration found for server type: " + serverType, 
                        "Server configuration not found");
            }
            
            if (!config.isConfigured() || !config.isEnabled()) {
                throw new ConfigurationException(ErrorCode.CONFIGURATION_ERROR, 
                        "Server is not configured or enabled: " + serverType, 
                        "Server is not configured");
            }
            
            // Check if the token is expired and refresh if needed
            if (oauth2Service.isAccessTokenExpired(config.getAccessTokenExpiry())) {
                LogUtils.logInfo(log, "Access token is expired for server type: {}, refreshing", serverType);
                refreshToken(config);
            }
            
            return createClientForConfig(config);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.logError(log, "Error creating metrics API client for server type: " + serverType, e);
            throw new ConfigurationException(ErrorCode.CONFIGURATION_ERROR, 
                    "Error creating metrics API client: " + e.getMessage(), 
                    "Failed to create metrics client", e);
        }
    }
    
    private MetricsApiClient createClientForConfig(ServerConfigBuilder config) throws ConfigurationException {
        String serverType = config.getServerType();
        
        if ("DatadogMetricServer".equals(serverType)) {
            return new DatadogMetricsApiClient(config.getAccessToken());
        } else {
            throw new ConfigurationException(ErrorCode.CONFIGURATION_ERROR, 
                    "Unsupported server type: " + serverType, 
                    "Unsupported metrics server type");
        }
    }
    
    private void refreshToken(ServerConfigBuilder config) throws ConfigurationException {
        try {
            // Create OAuth2Configuration from ServerConfigBuilder
            // Use OAuth2Service to refresh the token
            // Update the ServerConfigBuilder with the new token
            // Save the updated config
            
            // This is a placeholder for the actual implementation
            // The real implementation would use the OAuth2Service to refresh the token
            
            throw new ConfigurationException(ErrorCode.TOKEN_REFRESH_FAILED, 
                    "Token refresh is not implemented yet", 
                    "This feature is not available yet");
        } catch (Exception e) {
            LogUtils.logError(log, "Error refreshing token for server type: " + config.getServerType(), e);
            throw new ConfigurationException(ErrorCode.TOKEN_REFRESH_FAILED, 
                    "Error refreshing token: " + e.getMessage(), 
                    "Failed to refresh authentication token", e);
        }
    }
}
