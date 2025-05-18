package com.ddlabs.atlassian.auth;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.model.ServerConfigModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

@Path("/")
public class OauthClientServlet {
    private static final String PLUGIN_STORAGE_KEY = "com.ddlabs.atlassian.dd-bamboo-metrics";
    private static final Logger log = LoggerFactory.getLogger(OauthClientServlet.class);

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    private final UserService userService;
    private final OAuth2Authorization auth2Authorization;
    private final ConcurrentHashMap<String, String> condeVerifiers = new ConcurrentHashMap<>();
    @Inject
    public OauthClientServlet(PluginSettingsFactory pluginSettingsFactory, UserService userService, OAuth2Authorization auth2Authorization) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.userService = userService;
        this.auth2Authorization = auth2Authorization;
    }

    @POST
    @Path("authorize")
    @Produces(MediaType.TEXT_PLAIN)
    public Response authorize(final ServerConfigModel config, @Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
//        String oauth_request_url;
        try {
            String codeVerifier = OAuthPKCSCodeChallenge.generateCodeVerifier();
            String codeChallenge = OAuthPKCSCodeChallenge.generateCodeChallenge(codeVerifier);
            req.getSession().setAttribute("codeVerifier", userService.encrypt(codeVerifier));
            req.getSession().setAttribute("codeChallenge",userService.encrypt(codeChallenge));

//            oauth_request_url = auth2Authorization.buildAuthorizationUrl(
//                    config.getServerUrl(),
//                    "ac50d776-334e-11f0-bc4f-da7ad0900002",
//                    "http://localhost:6990/bamboo/rest/metrics/1.0/authorize",
//                    "code",
//                    codeChallenge,
//                    "S256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Response.ok("Ok").build();

    }
    @GET
    @Path("authorize")
    public Response redirectFromServer(@Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
        // Retrieve the code verifier and challenge from the session
        Object codeVerifier = req.getSession().getAttribute("codeVerifier");
        String code = req.getParameter("code");
        String clientId = req.getParameter("client_id");
        String site = req.getParameter("site");
        String domain = req.getParameter("domain");
        String dd_oid = req.getParameter("dd_oid");
        String dd_org_name = req.getParameter("dd_org_name");
        String secret = "ddocs_Y6bDXR7xskJtfDA3ZZlo7NJ6e44T1bb";
        String redirect = "http://localhost:6990/bamboo/rest/metrics/1.0/authorize";
        //save the variables in the database
        String response = exchangeAuthorizationCodeForAccessToken(code, clientId,
                URLDecoder.decode(site, StandardCharsets.UTF_8), domain, dd_oid, dd_org_name,
                secret,redirect, userService.decrypt(String.valueOf(codeVerifier)));
        // Convert string to JSON object
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
// Extract elements from the JSON object
        String accessToken = jsonObject.get("access_token").getAsString();
        int expiresIn = jsonObject.get("expires_in").getAsInt();
        String tokenType = jsonObject.get("token_type").getAsString();
        String scope = jsonObject.get("scope").getAsString();
        String refreshToken = jsonObject.get("refresh_token").getAsString();
        // Print the extracted values
        log.info("Access Token: {}" ,userService.encrypt(accessToken));
        log.info("Expires In: {}" ,userService.encrypt(String.valueOf(expiresIn)));
        log.info("Token Type: {}",userService.encrypt(tokenType));
        log.info("Scope: {}", userService.encrypt(scope));
        log.info("Refresh Token: {}",userService.encrypt(refreshToken));


        return Response.temporaryRedirect(URI.create("http://localhost:6990/bamboo/plugins/servlet/metrics")).build();
    }
    // Exchange the authorization code for an access token
    private String exchangeAuthorizationCodeForAccessToken(String code, String clientId, String site,
                                                            String domain, String dd_oid, String dd_org_name,
                                                String clientSecret,
                                                String redirectUri, String codeVerifier) {
        return auth2Authorization.exchangeAuthorizationCodeForAccessToken(domain,
                redirectUri, clientId, clientSecret, "authorization_code", codeVerifier, code);
    }


}
