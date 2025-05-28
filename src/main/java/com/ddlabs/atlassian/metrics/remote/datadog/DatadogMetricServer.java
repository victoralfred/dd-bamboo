package com.ddlabs.atlassian.metrics.remote.datadog;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.auth.OAuthPKCSCodeChallenge;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.metrics.model.*;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.util.HelperUtil;
import com.ddlabs.atlassian.util.exceptions.NullOrEmptyFieldsException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Objects;
import com.ddlabs.atlassian.metrics.model.MSConfig;

@Component
public class DatadogMetricServer implements MetricServer {
    private static final Logger log = LoggerFactory.getLogger(DatadogMetricServer.class);
    private static final String TOKEN_ENDPOINT = "https://api.datadoghq.com/oauth2/v1/token";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private final OAuth2AuthorizationService auth2Authorization;
    private final UserService userService;
    private final PluginDaoRepository pluginDao;
    private final ServerBodyBuilder serverBodyBuilder;
    public DatadogMetricServer(OAuth2AuthorizationService auth2Authorization,
                               UserService userService,
                               PluginDaoRepository pluginDao,
                               ServerBodyBuilder serverBodyBuilder) {
        this.auth2Authorization = Objects.requireNonNull(auth2Authorization, "auth2Authorization must not be null");
        this.userService = Objects.requireNonNull(userService, "userService must not be null");
        this.pluginDao = Objects.requireNonNull(pluginDao, "pluginDao must not be null");
        this.serverBodyBuilder = Objects.requireNonNull(serverBodyBuilder, "serverBodyBuilder must not be null");
    }
    @Override
    public String setupOauth2Authentication(String serverName) throws NullOrEmptyFieldsException {
        try {
            HelperUtil.checkNotNull(serverName, "serverName must not be null");
            MSConfig config = pluginDao.getServerConfigByType(serverName);
            if (config == null) {
                log.error("No server configuration found for server: {}", serverName);
                return null;
            }
            String codeChallenge = userService.decrypt(config.getCodeChallenge());
            return auth2Authorization.buildAuthorizationUrl(
                    config.getApiEndpoint(),
                    config.getClientId(),
                    config.getRedirectUrl(),
                    "code",
                    codeChallenge,
                    "S256"
            );
        } catch (Exception e) {
            throw new NullOrEmptyFieldsException(e);
        }
    }

    @Override
    public String getAccessToken(HttpServletRequest req, String serverName) throws NullOrEmptyFieldsException{
        try {
            HelperUtil.checkNotNullOrEmptyStrings(serverName);
            HelperUtil.checkNotNull(req);
            MSConfig config = pluginDao.getServerConfigByType(serverName);
            if (config == null || req.getParameter("code") == null) {
                log.error("Missing configuration or authorization code.");
                return null;
            }
            return auth2Authorization.exchangeAuthorizationCodeForAccessToken(
                    config.getRedirectUrl(),
                    req.getParameter("client_id"),
                    userService.decrypt(config.getClientSecret()),
                    GRANT_TYPE_AUTHORIZATION_CODE,
                    userService.decrypt(config.getCodeVerifier()),
                    req.getParameter("code"),
                    TOKEN_ENDPOINT
            );
        } catch (Exception ex) {
            log.error("Error exchanging authorization code for access token: ", ex);
            throw new NullOrEmptyFieldsException(ex);
        }
    }
    @Override
    public String saveServerMetadata(String serverType, String response, HttpServletRequest req) throws
    NullOrEmptyFieldsException{
        try {
            HelperUtil.checkNotNullOrEmptyStrings(serverType,response);
            HelperUtil.checkNotNull(req);
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            int expiresIn = json.get("expires_in").getAsInt();
            MSConfig config = pluginDao.getServerConfigByType(serverType);
            HelperUtil.checkNotNull(config);
            updateServerConfigFromResponse(config, json, req, expiresIn);
            pluginDao.updateServerConfig(config);
            return "success";
        } catch (Exception e) {
            log.error("Failed to parse or process response: ", e);
            throw new NullOrEmptyFieldsException(e);
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
    @Override
    public String saveServer(ServerConfigBody serverConfig)
            throws NullOrEmptyFieldsException {
        try {
            HelperUtil.checkNotNull(serverConfig);
            ServerConfigProperties properties = prepareServerProperties(serverConfig);
            HelperUtil.checkNotNull(properties);
            return pluginDao.saveServerConfig(properties);
        } catch (NoSuchAlgorithmException|NullOrEmptyFieldsException e) {
            log.error("Error generating code challenge for server config", e);
            throw new NullOrEmptyFieldsException(e);
        }
    }

    private String getJsonString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            throw new IllegalArgumentException("Missing or null key in JSON: " + key);
        }
        return json.get(key).getAsString();
    }
    private void updateServerConfigFromResponse(MSConfig config, JsonObject json, HttpServletRequest req, int expiresIn)
    throws NullOrEmptyFieldsException {
        try{
            HelperUtil.checkNotNull(config, json, req, expiresIn);
            config.setAccessToken(userService.encrypt(getJsonString(json, "access_token")));
            config.setRefreshToken(userService.encrypt(getJsonString(json, "refresh_token")));
            config.setTokenType(getJsonString(json, "token_type"));
            config.setScope(getJsonString(json, "scope"));
            config.setAccessTokenExpiry(Instant.now().plusSeconds(expiresIn).getEpochSecond());
            config.setConfigured(true);
            config.setEnabled(true);
            config.setSite(req.getParameter("site"));
            config.setDomain(req.getParameter("domain"));
            config.setOrgId(req.getParameter("dd_oid"));
            config.setOrgName(req.getParameter("dd_org_name"));
        }catch (NullOrEmptyFieldsException exception){
            log.error("Null or empty fields in server response: ", exception);
            throw new NullOrEmptyFieldsException(exception);
        }

    }
    private ServerConfigProperties prepareServerProperties(ServerConfigBody serverConfig)
            throws NoSuchAlgorithmException, NullOrEmptyFieldsException {
    try {
        HelperUtil.checkNotNull(serverConfig);
        final long CODE_VALIDITY_DURATION_SECONDS = 36 * 1000L;
        ServerConfigProperties properties = serverBodyBuilder.apply(serverConfig);
        String codeVerifier = OAuthPKCSCodeChallenge.generateCodeVerifier();
        String codeChallenge = OAuthPKCSCodeChallenge.generateCodeChallenge(codeVerifier);
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
}
