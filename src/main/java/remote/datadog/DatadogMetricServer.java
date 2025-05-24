package remote.datadog;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.auth.OAuthPKCSCodeChallenge;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import remote.MetricServer;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Component
public class DatadogMetricServer implements MetricServer {
    private final OAuth2AuthorizationService auth2Authorization;
    private final UserService userService;
    private final PluginDaoRepository pluginDao;
    private final ServerBodyBuilder serverBodyBuilder;
    private final Logger log = LoggerFactory.getLogger(DatadogMetricServer.class);
    public DatadogMetricServer(OAuth2AuthorizationService auth2Authorization,
                               UserService userService,
                               PluginDaoRepository pluginDao, ServerBodyBuilder serverBodyBuilder) {
        this.auth2Authorization = auth2Authorization;
        this.userService = userService;
        this.pluginDao = pluginDao;
        this.serverBodyBuilder = serverBodyBuilder;
    }

    @Override
    public String setupOauth2Authentication(String serverName) {
        MSConfig config  = pluginDao.getServerConfigByType(serverName);
        String serverDomain = config.getApiEndpoint();
        String clientId = config.getClientId();
        String redirectUri = config.getRedirectUrl();
        String codeChallenge = userService.decrypt(config.getCodeChallenge());
        return auth2Authorization.buildAuthorizationUrl(serverDomain, clientId,redirectUri,
                            "code", codeChallenge,"S256");
    }

    @Override
    public String getAccessToken(final HttpServletRequest req, String serverName) {
        MSConfig config  = pluginDao.getServerConfigByType(serverName);
        if (config == null || req.getParameter("code") == null) {
            return null;
        }
        final String redirect_url = config.getRedirectUrl();
        final String clientId = req.getParameter("client_id");
        final String clientSecret = userService.decrypt(config.getClientSecret());
        final String grant_type = "authorization_code";
        final String codeVerifier = userService.decrypt(config.getCodeVerifier());
        final String code = req.getParameter("code");
        final String tokenEndpoint = "https://api.datadoghq.com/oauth2/v1/token";
        return auth2Authorization.exchangeAuthorizationCodeForAccessToken(redirect_url,
                clientId,clientSecret,grant_type,codeVerifier,code,tokenEndpoint);
    }

    @Override
    public String saveServerMetadata(String serverType, String response, HttpServletRequest req) {
        // Check if the response is null or empty
        if (response == null || response.isEmpty()) {
            log.error("Received empty response from Datadog API");
            return "error";
        }
        // Log the response for debugging purposes
        log.info("Received response: {}", response);
        // Check if the serverType is valid
        if (serverType == null || serverType.isEmpty()) {
            log.error("Invalid server type provided: {}", serverType);
            return "error";
        }
        String site = req.getParameter("site");
        String domain = req.getParameter("domain");
        String dd_oid = req.getParameter("dd_oid");
        String dd_org_name = req.getParameter("dd_org_name");
        // Convert string to JSON object,
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        // Extract elements from the JSON object
        String accessToken = jsonObject.get("access_token").getAsString();
        int expiresIn = jsonObject.get("expires_in").getAsInt();
        String tokenType = jsonObject.get("token_type").getAsString();
        String scope = jsonObject.get("scope").getAsString();
        String refreshToken = jsonObject.get("refresh_token").getAsString();
        MSConfig msConfig = pluginDao.getServerConfigByType(serverType);
        msConfig.setAccessToken(userService.encrypt(accessToken));
        msConfig.setRefreshToken(userService.encrypt(refreshToken));
        msConfig.setTokenType(tokenType);
        msConfig.setScope(scope);
        msConfig.setAccessTokenExpiry(Instant.now().plusSeconds(expiresIn).getEpochSecond());
        msConfig.setConfigured(true);
        msConfig.setEnabled(true);
        msConfig.setSite(site);
        msConfig.setDomain(domain);
        msConfig.setOrgId(dd_oid);
        msConfig.setOrgName(dd_org_name);
        pluginDao.updateServerConfig(msConfig);
        return "success";
    }

    @Override
    public ConfigDefaults getConfigDefaults() {
        //String baseUrl = Optional.ofNullable(System.getProperty("baseurl")).orElse("${BAMBOO_BASE_URL}");
        return new ConfigDefaults(
                "https://api.datadoghq.com",
                "https://app.datadoghq.com/oauth2/v1/authorize",
                "https://api.datadoghq.com/oauth2/v1/token",
                 "http://localhost:6990/bamboo/rest/metrics/1.0/token"
        );
    }

    @Override
    public String saveServer(ServerConfigBody serverConfig) {
        // When saving the server property, generate the code verifier and code challenge
        // to be used for the next oauth verification process
        try{
            Instant now = Instant.now();
            ServerConfigProperties properties = serverBodyBuilder.apply(serverConfig);
            String codeVerifier = OAuthPKCSCodeChallenge.generateCodeVerifier();
            String codeChallenge = OAuthPKCSCodeChallenge.generateCodeChallenge(codeVerifier);
            properties.setCodeVerifier(userService.encrypt(codeVerifier));
            properties.setCodeChallenge(userService.encrypt(codeChallenge));
            properties.setClientSecret(userService.encrypt(properties.getClientSecret())); // Encrypt the client secret
            properties.setCodeCreationTime(now.toEpochMilli());
            properties.setCodeExpirationTime(now.plusSeconds(36*1000).getEpochSecond());
           return pluginDao.saveServerConfig(properties);
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

}
