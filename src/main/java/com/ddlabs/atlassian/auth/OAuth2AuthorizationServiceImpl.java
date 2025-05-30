package com.ddlabs.atlassian.auth;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.auth.oauth2.model.GrantType;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.http.HttpClient;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.util.HelperUtil;
import com.ddlabs.atlassian.exception.NullOrEmptyFieldsException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Locale;

import static com.ddlabs.atlassian.util.HelperUtil.getJsonString;

@BambooComponent
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthorizationServiceImpl.class);
    private final HttpClient connectionFactory;
    private final PluginDaoRepository pluginDaoRepository;
    private final UserService userService;
    
    public OAuth2AuthorizationServiceImpl(HttpClient connectionFactory,
                                         PluginDaoRepository pluginDaoRepository, 
                                         UserService userService) {
        this.connectionFactory = connectionFactory;
        this.pluginDaoRepository = pluginDaoRepository;
        this.userService = userService;
    }

    
    @Override
    public String exchangeRefreshTokenForAccessToken(String grantType, 
                                                   String refreshToken, 
                                                   String clientKey,
                                                   String clientSecrete, 
                                                   String tokenEndpoint) {
        try {
            HelperUtil.checkNotNullOrEmptyStrings(grantType, refreshToken, clientKey, clientSecrete, tokenEndpoint);
            String urlParameters = buildRefreshTokenUrl(grantType, refreshToken, clientKey, clientSecrete);
            return connectionFactory.post(
                    tokenEndpoint,
                    urlParameters,
                    "application/x-www-form-urlencoded"
            );
        } catch (NullOrEmptyFieldsException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void refreshToken(MetricServer metricServer) throws Exception {
        Assert.notNull(metricServer, "Server type cannot be null");
        String serverType = metricServer.getClass().getSimpleName();
         MSConfigEntity msConfigEntity =  pluginDaoRepository.getServerConfigByType(serverType);
        if (msConfigEntity==null) {
            log.warn("No configuration found for server: {}", serverType);
            return;
        }
        if (isAccessTokenExpired(msConfigEntity.getAccessTokenExpiry())) {
            refreshAndUpdateToken(msConfigEntity, serverType);
        } else {
            log.info("Access token for server {} is still valid.", serverType);
        }
    }
    
    private void refreshAndUpdateToken(MSConfigEntity msConfigEntity, String serverType) throws Exception {
        try {
            String response = exchangeRefreshTokenForAccessToken(
                    GrantType.REFRESH_TOKEN.toString().toLowerCase(Locale.ROOT),
                    msConfigEntity.getRefreshToken(),
                    msConfigEntity.getClientId(),
                    userService.decrypt(msConfigEntity.getClientSecret()),
                    msConfigEntity.getTokenEndpoint()
            );
            
            updateConfigWithNewTokens(msConfigEntity, response);
            pluginDaoRepository.updateServerConfig(msConfigEntity);
        } catch (Exception e) {
            log.error("Failed to refresh access token for server: {}", serverType, e);
            throw new Exception("Failed to refresh access token for server: " + serverType, e);
        }
    }
    
    private void updateConfigWithNewTokens(MSConfigEntity msConfigEntity, String response) {
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        int expiresIn = json.get("expires_in").getAsInt();
        
        msConfigEntity.setAccessToken(getJsonString(json, "access_token"));
        msConfigEntity.setRefreshToken(getJsonString(json, "refresh_token"));
        msConfigEntity.setTokenType(getJsonString(json, "token_type"));
        msConfigEntity.setScope(getJsonString(json, "scope"));
        msConfigEntity.setAccessTokenExpiry(Instant.now().plusSeconds(expiresIn).getEpochSecond());
    }
}
