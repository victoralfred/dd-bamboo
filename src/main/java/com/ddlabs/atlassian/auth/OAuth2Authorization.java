package com.ddlabs.atlassian.auth;

import com.atlassian.sal.core.util.Assert;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class OAuth2Authorization {
    /**
     * This method is used to build the authorization URL for the OAuth2 authorization code flow.
     *
     * @param domain          The domain of the Atlassian instance.
     * @param clientId        The client ID of the application.
     * @param redirectUri     The redirect URI used in the authorization request.
     * @param responseType    The response type (e.g., "code").
     * @param code_challenge  The code challenge used in the authorization request.
     * @param codeChallengeMethod The method used to generate the code challenge (e.g., "S256").
     * @return The authorization URL as a string.
     */
    public static String buildAuthorizationUrl(String domain, String clientId, String redirectUri,
                                               String responseType, String code_challenge,
                                               String codeChallengeMethod){
        final String OAUTH_ENDPOINT = "oauth2/v1/authorize";
        final String AUTH_ENDPOINT = "https://api." + domain + "/" + OAUTH_ENDPOINT;
        return AUTH_ENDPOINT +
                "&redirect_uri=" + redirectUri +
                "&client_id=" + clientId +
                "&response_type=" + responseType +
                "&code_challenge=" + code_challenge +
                "&code_challenge_method=" + codeChallengeMethod;
    }

    /**
     * This method is used to exchange the authorization code for an access token.
     *
     * @param domain       The domain of the Atlassian instance.
     * @param clientId     The client ID of the application.
     * @param clientSecret The client secret of the application.
     * @param code         The authorization code received from the authorization server.
     * @param redirectUri  The redirect URI used in the authorization request.
     * @param codeVerifier The code verifier used in the authorization request.
     * @return The access token as a string.
     */
    public static String exchangeAuthorizationCodeForAccessToken(String domain, String clientId,
                                               String clientSecret, String code, String redirectUri,
                                               String codeVerifier) {
        Assert.notNull(domain, "Domain cannot be null");
        Assert.notNull(clientId, "Client ID cannot be null");
        Assert.notNull(clientSecret, "Client Secret cannot be null");
        Assert.notNull(code, "Authorization code cannot be null");
        Assert.notNull(redirectUri, "Redirect URI cannot be null");
        Assert.notNull(codeVerifier, "Code Verifier cannot be null");
        String tokenEndpoint = "https://api." + domain + "/oauth2/v1/token";
        String urlParameters = constAuthorizationCodeForAccessTokenUrl(clientId, clientSecret,
                redirectUri, code, codeVerifier);
        try {
            HttpsURLConnection con = getHttpsURLConnection(new URI(tokenEndpoint), urlParameters);
            return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException | URISyntaxException e) {
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
    private static @NotNull HttpsURLConnection getHttpsURLConnection(URI uri, String urlParameters) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) uri.toURL().openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept", "application/json");
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return con;
    }
    private static String constAuthorizationCodeForAccessTokenUrl(String clientId, String clientSecret,
                                            String redirectUri, String code, String codeVerifier) {
        return "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + redirectUri +
                "&code=" + code +
                "&code_verifier=" + codeVerifier +
                "&grant_type=authorization_code";
    }
    private static String getAuthorizationCode(Map<String, String> params) {
        String code = null;
        if (params != null && params.containsKey("code")) {
            code = params.get("code");
        }
        return code;
    }
}
