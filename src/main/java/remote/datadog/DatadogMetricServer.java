package remote.datadog;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.auth.OAuthPKCSCodeChallenge;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.model.MSConfig;
import com.ddlabs.atlassian.model.ServerConfigProperties;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import remote.MetricServer;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@Component
public class DatadogMetricServer implements MetricServer {
    private final OAuth2AuthorizationService auth2Authorization;
    private final UserService userService;
    private final PluginDaoRepository pluginDao;
    private final Logger log = LoggerFactory.getLogger(DatadogMetricServer.class);
    public DatadogMetricServer(OAuth2AuthorizationService auth2Authorization, UserService userService, PluginDaoRepository pluginDao) {
        this.auth2Authorization = auth2Authorization;
        this.userService = userService;
        this.pluginDao = pluginDao;
    }
    @Override
    public String setupOauth2Authentication(String serverName) {
        MSConfig config  = pluginDao.getServerConfigByName(serverName);
        String serverDomain = config.getDomain();
        String clientId = config.getClientId();
        String redirectUri = config.getRedirectUrl();
        String codeChallenge = userService.decrypt(config.getCodeChallenge());
        return auth2Authorization.buildAuthorizationUrl(serverDomain, clientId,redirectUri,
                            "code", codeChallenge,"S256");
    }
    @Override
    public String getAccessToken(final HttpServletRequest req, String serverName) {
        MSConfig config  = pluginDao.getServerConfigByName(serverName);
        if (config == null || req.getParameter("code") == null) {
            return null;
        }
        final String redirect_url = config.getRedirectUrl();
        final String clientId = req.getParameter("client_id");
        final String clientSecret = config.getClientSecret();
        final String grant_type = "authorization_code";
        final String codeVerifier = config.getCodeVerifier();
        final String code = req.getParameter("code");
        final String tokenEndpoint = "https://api.datadoghq.com/oauth2/v1/token";
        return auth2Authorization.exchangeAuthorizationCodeForAccessToken(redirect_url, code,
                clientId, clientSecret,grant_type ,
                codeVerifier, tokenEndpoint);
    }

    @Override
    public String saveServerMetadata(String serverType, String response) {
        // Convert string to JSON object,
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        // Extract elements from the JSON object
        String accessToken = jsonObject.get("access_token").getAsString();
        int expiresIn = jsonObject.get("expires_in").getAsInt();
        String tokenType = jsonObject.get("token_type").getAsString();
        String scope = jsonObject.get("scope").getAsString();
        String refreshToken = jsonObject.get("refresh_token").getAsString();
        MSConfig msConfig = pluginDao.getServerConfigByName(serverType);
        msConfig.setAccessToken(userService.encrypt(accessToken));
        msConfig.setRefreshToken(userService.encrypt(refreshToken));
        msConfig.setTokenType(tokenType);
        msConfig.setScope(scope);
        msConfig.setAccessTokenExpiredIn(expiresIn);
        pluginDao.updateServerConfig(msConfig);
        return "success";
    }
    @Override
    public String saveServer(ServerConfigProperties serverConfig) {
        // When saving the server property, generate the code verifier and code challenge
        // to be used for the next oauth verification process
        try{
            String codeVerifier = OAuthPKCSCodeChallenge.generateCodeVerifier();
            serverConfig.setCodeVerifier(codeVerifier);
            serverConfig.setCodeChallenge(OAuthPKCSCodeChallenge.generateCodeChallenge(codeVerifier));
           return pluginDao.saveServerConfig(serverConfig);
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
}
