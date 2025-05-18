package com.ddlabs.atlassian.auth;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.sal.core.util.Assert;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@BambooComponent
public class OAuth2Authorization {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Authorization.class);

    /**
     * This method is used to build the authorization URL for the OAuth2 authorization code flow.
     *
     * @param domain          The domain of the Metric Server instance.
     * @param clientId        The client ID of the application.
     * @param redirectUri     The redirect URI used in the authorization request.
     * @param responseType    The response type (e.g., "code").
     * @param code_challenge  The code challenge used in the authorization request.
     * @param codeChallengeMethod The method used to generate the code challenge (e.g., "S256").
     * @return The authorization URL as a string.
     */
    public String buildAuthorizationUrl(String domain, String clientId,
                                        String redirectUri, String responseType,
                                        String code_challenge,
                                       String codeChallengeMethod){
        final String OAUTH_ENDPOINT = "oauth2/v1/authorize";
        final String AUTH_ENDPOINT = domain + "/" + OAUTH_ENDPOINT;
        return AUTH_ENDPOINT +"?"+
                "redirect_uri=" + redirectUri +
                "&client_id=" + clientId +
                "&response_type=" + responseType +
                "&code_challenge=" + code_challenge +
                "&code_challenge_method=" + codeChallengeMethod;
    }


    public String exchangeAuthorizationCodeForAccessToken(String domain, String redirectUri, String clientId,
                                               String clientSecret, String grantType, String codeVerifier,
                                                          String code ) {
        Assert.notNull(domain, "Domain cannot be null");
        Assert.notNull(redirectUri, "Redirect URI cannot be null");
        Assert.notNull(clientId, "Client ID cannot be null");
        Assert.notNull(clientSecret, "Client Secret cannot be null");
        Assert.notNull(grantType, "Grant type cannot be null");
        Assert.notNull(codeVerifier, "Code Verifier cannot be null");
        Assert.notNull(code, "Authorization code cannot be null");

        String tokenEndpoint = "https://api.datadoghq.com/oauth2/v1/token";
        String urlParameters = constAuthorizationCodeForAccessTokenUrl(clientId, clientSecret,
                redirectUri, code, codeVerifier);
        log.debug("urlParameters: {}", urlParameters);
        try {
            HttpsURLConnection con = getUrlConnection(new URI(tokenEndpoint), urlParameters);
            return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public void openOAuthServerTab(String serverUrl) {
        String os = System.getProperty("os.name");
        try {
            if(os.toLowerCase().contains("windows")) {
                Runtime.getRuntime().exec("cmd /c start " + serverUrl);
            } else if(os.toLowerCase().contains("mac")) {
                Runtime.getRuntime().exec("open " + serverUrl);
            } else if(os.toLowerCase().contains("nix") || os.toLowerCase().contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + serverUrl);
            }else {
                log.error("Unsupported OS: {}" , os);
            }
        } catch (Exception e) {
            log.error("Error opening OAuth Server Tab", e);
            throw new RuntimeException(e);
        }

    }
    private static String getAccessToken(String response) {
        String accessToken = null;
        if (response != null && response.contains("access_token")) {
            String[] parts = response.split("&");
            for (String part : parts) {
                if (part.startsWith("access_token=")) {
                    accessToken = part.substring("access_token=".length());
                    break;
                }
            }
        }
        return accessToken;
    }
    private static @NotNull HttpsURLConnection getUrlConnection(URI uri, String urlParameters) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) uri.toURL().openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        //log the request body
        log.info("Request URL: {}", uri);
        log.info("Request Method: {}", con.getRequestMethod());
        con.getRequestProperties().entrySet().iterator().forEachRemaining(entry -> {
            log.info("Request Header: {}: {}", entry.getKey(), entry.getValue());
        });
        log.info("Request Body: {}", urlParameters);
        // Send post request

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }
        return con;
    }

    private static String constAuthorizationCodeForAccessTokenUrl(String clientId, String clientSecret,
                                            String redirectUri, String code, String codeVerifier) {
        return "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                "&redirect_uri=" + redirectUri +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&code_verifier=" + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8) +
                "&grant_type=authorization_code";
    }
}
