package com.ddlabs.atlassian.impl.metrics;

import com.ddlabs.atlassian.impl.cache.MetricServerConfigurationCache;
import com.ddlabs.atlassian.impl.config.model.*;
import com.ddlabs.atlassian.impl.events.localevents.PluginServerDeletedEvent;
import com.ddlabs.atlassian.impl.events.localevents.ServerDeletedEvent;
import com.ddlabs.atlassian.impl.exception.*;
import com.ddlabs.atlassian.oauth2.CodeVerifierAndChallengeProvider;
import com.ddlabs.atlassian.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.api.OAuth2Service;
import com.ddlabs.atlassian.impl.config.UserService;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigMapper;
import com.ddlabs.atlassian.dao.adapter.entity.ConfigRepository;
import com.ddlabs.atlassian.api.MetricsApiClient;
import com.ddlabs.atlassian.impl.metrics.factory.MetricsApiClientFactory;
import com.ddlabs.atlassian.api.MetricServer;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 * Datadog implementation of the MetricServer interface.
 */
@Component
public class DatadogMetricServer implements MetricServer {
    private static final Logger log = LoggerFactory.getLogger(DatadogMetricServer.class);
    private static final String TOKEN_ENDPOINT = "https://api.datadoghq.com/oauth2/v1/token";
    private final ServerConfigMapper serverConfigMapper;

    private final OAuth2Service oauth2Service;
    private final UserService userService;
    private final ConfigRepository configRepository;
    private final ServerBodyBuilder serverBodyBuilder;
    private final MetricsApiClientFactory metricsApiClientFactory;
    private final MetricServerConfigurationCache metricServerConfigurationCache;

    public DatadogMetricServer(ServerConfigMapper serverConfigMapper, OAuth2Service oauth2Service,
                               UserService userService,
                               ConfigRepository configRepository,
                               ServerBodyBuilder serverBodyBuilder,
                               MetricsApiClientFactory metricsApiClientFactory, MetricServerConfigurationCache metricServerConfigurationCache, PluginServerDeletedEvent pluginServerDeletedEvent) {
        this.serverConfigMapper = serverConfigMapper;
        this.oauth2Service = ValidationUtils.validateNotNull(oauth2Service, "OAuth2Service cannot be null");
        this.userService = ValidationUtils.validateNotNull(userService, "UserService cannot be null");
        this.configRepository = ValidationUtils.validateNotNull(configRepository,
                "ConfigRepository cannot be null");
        this.serverBodyBuilder = ValidationUtils.validateNotNull(serverBodyBuilder,
                "ServerBodyBuilder cannot be null");
        this.metricsApiClientFactory = ValidationUtils.validateNotNull(metricsApiClientFactory,
                "MetricsApiClientFactory cannot be null");
        this.metricServerConfigurationCache = metricServerConfigurationCache;
        this.pluginServerDeletedEvent = pluginServerDeletedEvent;
    }
    
    @Override
    public String setupOauth2Authentication(String serverName) throws ValidationException {
        try {
            ValidationUtils.validateNotEmpty(serverName, "Server name cannot be empty");
            
            ServerConfigBuilder config =  configRepository.findByServerType(serverName);
            if (config == null) {
                LogUtils.logError(log, "No server configuration found for server: " + serverName, null);
                throw new ConfigurationException(ErrorCode.ENTITY_NOT_FOUND,
                        "No server configuration found for server: " + serverName,
                        "Server configuration not found");
            }
            String codeChallenge = userService.decrypt(config.getCodeChallenge());
            OAuth2Configuration oauth2Config = new OAuth2Configuration();
            oauth2Config.setApiEndpoint(config.getApiEndpoint());
            oauth2Config.setAuthEndpoint(config.getOauthEndpoint());
            oauth2Config.setClientId(config.getClientId());
            oauth2Config.setRedirectUri(config.getRedirectUrl());
            oauth2Config.setCodeChallenge(codeChallenge);
            oauth2Config.setCodeChallengeMethod("S256");
            return oauth2Service.generateAuthorizationUrl(oauth2Config);
        } catch (ConfigurationException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.logError(log, "Error setting up OAuth2 authentication", e);
            throw new ValidationException(
                    "Error setting up OAuth2 authentication: " + e.getMessage(),
                    "Failed to set up authentication");
        }
    }
    @Override
    public String getAccessToken(HttpServletRequest req, String serverName) throws ValidationException {
        try {
            ValidationUtils.validateNotEmpty(serverName, "Server name cannot be empty");
            ValidationUtils.validateNotNull(req, "HTTP request cannot be null");

            ServerConfigBuilder config =  configRepository.findByServerType(serverName);
            if (config == null) {
                LogUtils.logError(log, "No server configuration found for server: " + serverName, null);
                throw new ConfigurationException(ErrorCode.ENTITY_NOT_FOUND,
                        "No server configuration found for server: " + serverName,
                        "Server configuration not found");
            }
            
            String code = req.getParameter("code");
            if (code == null) {
                LogUtils.logError(log, "Missing authorization code", null);
                throw new ValidationException(
                        "Missing authorization code",
                        "Authorization code is required");
            }
            
            OAuth2Configuration oauth2Config = new OAuth2Configuration();
            oauth2Config.setTokenEndpoint(TOKEN_ENDPOINT);
            oauth2Config.setClientId(req.getParameter("client_id"));
            oauth2Config.setClientSecret(userService.decrypt(config.getClientSecret()));
            oauth2Config.setRedirectUri(config.getRedirectUrl());
            oauth2Config.setCodeVerifier(userService.decrypt(config.getCodeVerifier()));
            
            OAuth2TokenResponse tokenResponse = oauth2Service.exchangeCodeForTokens(code, oauth2Config);
            
            // Convert the token response to a string format for backward compatibility
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("access_token", tokenResponse.getAccessToken());
            jsonResponse.addProperty("refresh_token", tokenResponse.getRefreshToken());
            jsonResponse.addProperty("token_type", tokenResponse.getTokenType());
            jsonResponse.addProperty("scope", tokenResponse.getScope());
            jsonResponse.addProperty("expires_in", tokenResponse.getExpiresIn());
            
            return jsonResponse.toString();
        } catch (ConfigurationException | AuthenticationException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.logError(log, "Error exchanging authorization code for access token", e);
            throw new ValidationException(
                    "Error exchanging authorization code for access token: " + e.getMessage(),
                    "Failed to get access token");
        }
    }
    
    @Override
    public void saveServerMetadata(String serverType, String response, HttpServletRequest req) throws ValidationException {
        try {
            ValidationUtils.validateNotEmpty(serverType, "Server type cannot be empty");
            ValidationUtils.validateNotEmpty(response, "Response cannot be empty");
            ValidationUtils.validateNotNull(req, "HTTP request cannot be null");
            
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            int expiresIn = json.get("expires_in").getAsInt();

            ServerConfigBuilder config =  configRepository.findByServerType(serverType);
            if (config == null) {
                LogUtils.logError(log, "No server configuration found for server: " + serverType, null);
                throw new ConfigurationException(ErrorCode.ENTITY_NOT_FOUND,
                        "No server configuration found for server: " + serverType,
                        "Server configuration not found");
            }
            
            updateServerConfigFromResponse(config, json, req, expiresIn);
            configRepository.update(config);
            metricServerConfigurationCache.updateServerConfig(serverType);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.logError(log, "Failed to parse or process response", e);
            throw new ValidationException(
                    "Failed to parse or process response: " + e.getMessage(),
                    "Failed to save server metadata");
        }
    }
    
    @Override
    public ConfigDefaults getConfigDefaults() {
        return new ConfigDefaults(
                "https://api.datadoghq.com",
                "https://app.datadoghq.com/oauth2/v1/authorize",
                TOKEN_ENDPOINT,
                "http://localhost:6990/bamboo/rest/metrics/1.0/token"
        );
    }
   private final PluginServerDeletedEvent pluginServerDeletedEvent;
    @Override
    public String deleteServer(String serverName) {
        try{
            configRepository.delete(serverName);
            pluginServerDeletedEvent.publish(new ServerDeletedEvent(serverName));
            return "success";
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String saveServer(ServerConfigBody serverConfig) throws ValidationException {
        try {
            ValidationUtils.validateNotNull(serverConfig, "Server config cannot be null");
            ServerConfigProperties properties = prepareServerProperties(serverConfig);
            ValidationUtils.validateNotNull(properties, "Server properties cannot be null");
            ServerConfigBuilder configDTO = new ServerConfigBuilder();
            switch (serverConfig.getOauthOrApiKey()) {
                case "API_KEY":
                    serverConfigMapper.dtoObjectTransformer(configDTO,
                            serverConfig.getServerType(),serverConfig.getServerName(),serverConfig.getDescription(),serverConfig.getClientKey(),
                            serverConfig.getClientSecret(),"", "","",Instant.now().getEpochSecond(),
                            -100L,serverConfig.getApiEndpoint(),serverConfig.getOauthEndpoint(),
                            serverConfig.getTokenEndpoint(),"","","","", true, true,"","",
                            serverConfig.getOauthOrApiKey(),"API_ACCESS_SCOPE",-100L);
                    break;
                case "OAUTH":
                    serverConfigMapper.transForForAuthTokenRequest(configDTO,properties.getServerType(), properties.getServerName(),
                            properties.getDescription(), properties.getClientId(), properties.getClientSecret(),
                            properties.getRedirectUrl(), properties.getCodeVerifier(), properties.getCodeChallenge(),
                            properties.getCodeCreationTime(), properties.getCodeExpirationTime(),properties.getApiEndpoint(),
                            properties.getOauthEndpoint(), properties.getTokenEndpoint());
                    break;
            }
            String response = configRepository.save(configDTO);
            if(response.contains("success")) {
                metricServerConfigurationCache.updateServerConfig(serverConfig.getServerType());
                return response;
            }
            return response;
        } catch (Exception e) {
            LogUtils.logError(log, "Error generating code challenge for server config", e);
            throw new ValidationException(
                    "Error saving server configuration: " + e.getMessage(),
                    "Failed to save server configuration");
        }
    }
    
    /**
     * Gets a MetricsApiClient for the Datadog server.
     *
     * @return The MetricsApiClient
     * @throws ConfigurationException If an error occurs
     */
    public MetricsApiClient getMetricsApiClient() throws ConfigurationException {
        return metricsApiClientFactory.createClient(getClass().getSimpleName());
    }
    
    private void  updateServerConfigFromResponse(ServerConfigBuilder config, JsonObject json, HttpServletRequest req, int expiresIn) {
        ValidationUtils.validateNotNull(config, "Config cannot be null");
        ValidationUtils.validateNotNull(json, "JSON response cannot be null");
        ValidationUtils.validateNotNull(req, "HTTP request cannot be null");
        
        String accessToken = getJsonString(json, "access_token");
        String refreshToken = getJsonString(json, "refresh_token");
        String tokenType = getJsonString(json, "token_type");
        String scope = getJsonString(json, "scope");
        
        ValidationUtils.validateNotEmpty(accessToken, "Access token cannot be empty");
        ValidationUtils.validateNotEmpty(refreshToken, "Refresh token cannot be empty");
        
        config.setAccessToken(accessToken);
        config.setRefreshToken(refreshToken);
        config.setTokenType(tokenType);
        config.setScope(scope);
        config.setAccessTokenExpiry(Instant.now().plusSeconds(expiresIn).getEpochSecond());
        config.setConfigured(true);
        config.setEnabled(true);
        config.setSite(req.getParameter("site"));
        config.setDomain(req.getParameter("domain"));
        config.setOrgId(req.getParameter("dd_oid"));
        config.setOrgName(req.getParameter("dd_org_name"));
    }

    private ServerConfigProperties prepareServerProperties(ServerConfigBody serverConfig)
            throws NoSuchAlgorithmException, NullOrEmptyFieldsException {
        try {
            ValidationUtils.checkNotNull(serverConfig);
            final long CODE_VALIDITY_DURATION_SECONDS = 36 * 1000L;
            ServerConfigProperties properties = serverBodyBuilder.apply(serverConfig);
            String codeVerifier = CodeVerifierAndChallengeProvider.generateCodeVerifier();
            String codeChallenge = CodeVerifierAndChallengeProvider.generateCodeChallenge(codeVerifier);
            Instant now = Instant.now();
            properties.setCodeVerifier(userService.encrypt(codeVerifier));
            properties.setCodeChallenge(userService.encrypt(codeChallenge));
            properties.setClientSecret(userService.encrypt(properties.getClientSecret()));
            properties.setCodeCreationTime(now.toEpochMilli());
            properties.setCodeExpirationTime(now.plusSeconds(CODE_VALIDITY_DURATION_SECONDS).getEpochSecond());
            log.info("Prepared server properties for client ID: {}", properties.getClientId());
            return properties;
        }catch (NullOrEmptyFieldsException exception){
            log.error("Null or empty fields in server response: ", exception);
            throw new NullOrEmptyFieldsException(exception);
        }
    }
    
    private String getJsonString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return null;
        }
        return json.get(key).getAsString();
    }
}
