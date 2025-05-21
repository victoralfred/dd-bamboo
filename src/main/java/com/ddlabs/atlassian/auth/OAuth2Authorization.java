package com.ddlabs.atlassian.auth;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.api.HttpConnectionFactory;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.ddlabs.atlassian.model.ApplicationProperties.CONNECTION_TIMEOUT;
import static com.ddlabs.atlassian.model.ApplicationProperties.READ_TIMEOUT;

@BambooComponent
public class OAuth2Authorization {
    private final HttpConnectionFactory connectionFactory;
    public OAuth2Authorization(HttpConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    /**
     * Builds the authorization URL for OAuth2 authentication.
     *
     * @param domain            The domain of the API.
     * @param clientId          The client ID.
     * @param redirectUri       The redirect URI.
     * @param responseType      The response type (e.g., "code").
     * @param code_challenge    The code challenge.
     * @param codeChallengeMethod The code challenge method (e.g., "S256").
     * @return The authorization URL.
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
    /**
     * Exchanges the authorization code for an access token.
     *
     * @param domain            The domain of the API.
     * @param redirectUri       The redirect URI.
     * @param clientId          The client ID.
     * @param clientSecret      The client secret.
     * @param grantType         The grant type (e.g., "authorization_code").
     * @param codeVerifier      The code verifier.
     * @param code              The authorization code.
     * @return The access token response as a string.
     */
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
        String urlParameters = buildAuthorizationAccessURL(clientId, clientSecret,
                redirectUri, code, codeVerifier);
        try {
            HttpsURLConnection con = (HttpsURLConnection) getUrlConnection(
                    new URI(tokenEndpoint), urlParameters
            ,"POST","application/x-www-form-urlencoded");
            return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Creates an HTTP connection to the specified URI with the given parameters.
     *
     * @param uri              The URI to connect to.
     * @param urlParameters    The URL parameters to send in the request body.
     * @param method           The HTTP method (e.g., "POST").
     * @param mediaType        The media type (e.g., "application/x-www-form-urlencoded").
     * @return The HttpURLConnection object.
     * @throws IOException If an I/O error occurs.
     */
    @NotNull
    public HttpURLConnection getUrlConnection(URI uri, String urlParameters,
                                              String method, String mediaType) throws IOException {
        return openHttpsConnection(uri, urlParameters, method, mediaType);
    }
    public String buildAuthorizationAccessURL(String clientId, String clientSecret, String redirectUri, String code, String codeVerifier) {
        return constAuthorizationCodeForAccessTokenUrl(clientId, clientSecret,
                redirectUri, code, codeVerifier);
    }
    /**
     * Opens an HTTPS connection to the specified URI with the given parameters.
     *
     * @param uri              The URI to connect to.
     * @param urlParameters    The URL parameters to send in the request body.
     * @param method           The HTTP method (e.g., "POST").
     * @param mediaType        The media type (e.g., "application/x-www-form-urlencoded").
     * @return The HttpsURLConnection object.
     * @throws IOException If an I/O error occurs.
     */
    private HttpURLConnection openHttpsConnection(URI uri, String urlParameters,
                                            String method, String mediaType) throws IOException {
        HttpsURLConnection connection = connectionFactory.createConnection(uri,method,mediaType);
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", mediaType);
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }
        return connection;
    }
    /**
     * Constructs the URL parameters for the authorization code exchange.
     *
     * @param clientId          The client ID.
     * @param clientSecret      The client secret.
     * @param redirectUri       The redirect URI.
     * @param code              The authorization code.
     * @param codeVerifier      The code verifier.
     * @return The URL parameters as a string.
     */
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
