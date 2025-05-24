package com.ddlabs.atlassian.auth;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.sal.core.util.Assert;
import com.ddlabs.atlassian.api.HttpConnectionFactory;
import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@BambooComponent
public class OAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthorizationServiceImpl.class);
    private final HttpConnectionFactory connectionFactory;
    public OAuth2AuthorizationServiceImpl(HttpConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    @Override
    public String exchangeAuthorizationCodeForAccessToken(@NotNull final String redirectUri,
                                                          @NotNull final String clientId,
                                                          @NotNull final String clientSecret,
                                                          @NotNull final String grantType,
                                                          @NotNull final String codeVerifier,
                                                          @NotNull final String code,
                                                          @NotNull final String tokenEndpoint ) {
        Assert.notNull(redirectUri, "Redirect URI cannot be null");
        Assert.notNull(clientId, "Client ID cannot be null");
        Assert.notNull(clientSecret, "Client Secret cannot be null");
        Assert.notNull(grantType, "Grant type cannot be null");
        Assert.notNull(codeVerifier, "Code Verifier cannot be null");
        Assert.notNull(code, "Authorization code cannot be null");
        Assert.notNull(tokenEndpoint, "Token endpoint cannot be null");

        String urlParameters = buildAuthorizationAccessURL(clientId, clientSecret,
                redirectUri, code, codeVerifier);
        try {
            HttpsURLConnection con = (HttpsURLConnection) connectionFactory.getUrlConnection(
                    new URI(tokenEndpoint), urlParameters
            ,"POST","application/x-www-form-urlencoded");
            return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public String constAuthorizationCodeForAccessTokenUrl(@NotNull final String  clientId,
                                                          @NotNull final String clientSecret,
                                                          @NotNull final String redirectUri,
                                                          @NotNull final String code,
                                                          @NotNull final String codeVerifier) {
        return "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                "&redirect_uri=" + redirectUri +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&code_verifier=" + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8) +
                "&grant_type=authorization_code";
    }
    private String buildAuthorizationAccessURL(@NotNull final String clientId,
                                               @NotNull final String clientSecret,
                                               @NotNull final String redirectUri,
                                               @NotNull final String code,
                                               @NotNull final String codeVerifier) {
        return constAuthorizationCodeForAccessTokenUrl(clientId, clientSecret,
                redirectUri, code, codeVerifier);
    }

}
