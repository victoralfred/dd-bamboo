package com.ddlabs.atlassian.auth;


import com.ddlabs.atlassian.api.HttpConnectionFactory;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.config.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationServiceImplTest{
    @Mock
    private HttpConnectionFactory connectionFactory;
    @Mock
    private PluginDaoRepository pluginDaoRepository;
    @Mock
    private UserService userService;
    @Mock
    private HttpsURLConnection mockConnection;
    @InjectMocks
    private OAuth2AuthorizationServiceImpl authService;
    private final String clientId = "clientId";
    private final String clientSecret = "clientSecret";
    private final String grantType = "authorization_code";
    private final String codeVerifier = "codeVerifier";
    private final String code = "authCode";

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        authService = new OAuth2AuthorizationServiceImpl(connectionFactory, pluginDaoRepository, userService);
    }

    void testExchangeAuthorizationCodeForAccessToken_ReturnsAccessToken() throws Exception {
       // GIVEN
        String expectedResponse =  "Unexpected response code: 0";
        String tokenEndpoint = "https://example.com/token";
        URI uri = new URI(tokenEndpoint);
        // WHEN
        when(connectionFactory.getUrlConnection(eq(uri), anyString(), eq("POST"), eq("application/x-www-form-urlencoded")))
                .thenReturn(mockConnection);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8)));
        //THEN
        String redirectUri = "https://example.com/redirect";
        String result = authService.exchangeAuthorizationCodeForAccessToken(  redirectUri, clientId, clientSecret, grantType, codeVerifier, code, tokenEndpoint  );
        Assertions.assertEquals(expectedResponse, result);
    }

    void testConstAuthorizationCodeForAccessTokenUrl_ReturnsExpectedUrl() {
        // GIVEN
        String clientId = "myClient";
        String clientSecret = "mySecret";
        String redirectUri = "https://example.com/callback";
        String code = "authCode123";
        String codeVerifier = "codeVerifierXYZ";
        //WHEN
        String result = authService.constAuthorizationCodeForAccessTokenUrl(
                clientId, clientSecret, redirectUri, code, codeVerifier
        );
        // THEN
        Assertions.assertTrue(result.contains("client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)));
        Assertions.assertTrue(result.contains("client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)));
        Assertions.assertTrue(result.contains("redirect_uri=" + redirectUri));
        Assertions.assertTrue(result.contains("code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)));
        Assertions.assertTrue(result.contains("code_verifier=" + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8)));
        Assertions.assertTrue(result.contains("grant_type=authorization_code"));
    }
    void testExchangeAuthorizationCodeForAccessToken_ThrowsRuntimeExceptionOnInvalidUri() {
        // WHEN / THEN
        assertThrows(RuntimeException.class,
                () -> authService.exchangeAuthorizationCodeForAccessToken(
                "invalid_uri", clientId, clientSecret,
                        grantType, codeVerifier, code, "://"
        ));
    }
}