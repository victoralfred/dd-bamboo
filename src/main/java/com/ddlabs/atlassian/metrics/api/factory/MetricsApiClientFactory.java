package com.ddlabs.atlassian.metrics.api.factory;

import com.ddlabs.atlassian.auth.oauth2.service.OAuth2Service;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.repository.ServerConfigRepository;
import com.ddlabs.atlassian.exception.ConfigurationException;
import com.ddlabs.atlassian.exception.ErrorCode;
import com.ddlabs.atlassian.metrics.api.MetricsApiClient;
import com.ddlabs.atlassian.metrics.api.provider.DatadogMetricsApiClient;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating MetricsApiClient instances.
 */
public class MetricsApiClientFactory {
    private static final Logger log = LoggerFactory.getLogger(MetricsApiClientFactory.class);
    
    private final ServerConfigRepository serverConfigRepository;
    private final OAuth2Service oauth2Service;
    
    public MetricsApiClientFactory(ServerConfigRepository serverConfigRepository, OAuth2Service oauth2Service) {
        this.serverConfigRepository = ValidationUtils.validateNotNull(serverConfigRepository, 
                "ServerConfigRepository cannot be null");
        this.oauth2Service = ValidationUtils.validateNotNull(oauth2Service, 
                "OAuth2Service cannot be null");
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
            
            ServerConfigDTO config = serverConfigRepository.findByServerType(serverType);
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
    
    private MetricsApiClient createClientForConfig(ServerConfigDTO config) throws ConfigurationException {
        String serverType = config.getServerType();
        
        if ("DatadogMetricServer".equals(serverType)) {
            return new DatadogMetricsApiClient(config.getAccessToken());
        } else {
            throw new ConfigurationException(ErrorCode.CONFIGURATION_ERROR, 
                    "Unsupported server type: " + serverType, 
                    "Unsupported metrics server type");
        }
    }
    
    private void refreshToken(ServerConfigDTO config) throws ConfigurationException {
        try {
            // Create OAuth2Configuration from ServerConfigDTO
            // Use OAuth2Service to refresh the token
            // Update the ServerConfigDTO with the new token
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
