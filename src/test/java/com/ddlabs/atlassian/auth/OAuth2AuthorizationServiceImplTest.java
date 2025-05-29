package com.ddlabs.atlassian.auth;


import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.http.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationServiceImplTest{
    @Mock
    private HttpClient connectionFactory;
    @Mock
    private PluginDaoRepository pluginDaoRepository;
    @Mock
    private UserService userService;
    @Mock
    private HttpsURLConnection mockConnection;
    @InjectMocks
    private OAuth2AuthorizationServiceImpl authService;


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

        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8)));

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

    }
}