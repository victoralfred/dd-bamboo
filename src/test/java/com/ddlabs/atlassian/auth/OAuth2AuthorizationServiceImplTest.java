package com.ddlabs.atlassian.auth;


import com.ddlabs.atlassian.api.HttpConnectionFactory;
import junit.framework.TestCase;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.ddlabs.atlassian.model.ApplicationProperties.CONNECTION_TIMEOUT;
import static com.ddlabs.atlassian.model.ApplicationProperties.READ_TIMEOUT;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class OAuth2AuthorizationServiceImplTest {
        //extends TestCase {
//    private OAuth2AuthorizationService oauth2AuthorizationService;
//    @Mock
//    HttpsURLConnection mockedConnection;
//    @Mock
//    private HttpConnectionFactory mockFactory;
//
//    @Override
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        oauth2AuthorizationService = new OAuth2AuthorizationServiceImpl(mockFactory);
//    }
//    public void testBuildAuthorizationUrl() {
//        // Given
//        String domain = "https://api.example.com";
//        String clientId = "your_client_id";
//        String redirectUri = "https://api.yourapp.com/callback";
//        String responseType = "code";
//        String code_challenge = "your_code_challenge";
//        String codeChallengeMethod = "S256";
//        // When
//        String result = oauth2AuthorizationService.buildAuthorizationUrl(domain, clientId, redirectUri, responseType, code_challenge, codeChallengeMethod);
//        // Then
//        assertNotNull(result);
//        assertTrue(result.startsWith(domain + "/oauth2/v1/authorize"));
//    }
//    public void testBuildAuthorizationUrlWithCodeChallengeMethod() {
//        // Given
//        String clientId = "your_client_id";
//        String redirectUri = "https://api.yourapp.com/callback";
//        String clientSecret = "your_client_secret";
//        String code = "your_code";
//        String codeVerifier = "your_code_verifier";
//        URI tokenEndpoint = URI.create("https://api.example.com/token");
//        String expectedUrlParameters = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
//                "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
//                "&redirect_uri=" + redirectUri +
//                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
//                "&code_verifier=" + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8) +
//                "&grant_type=authorization_code";
//
//        try{
//            // Stub factory and connection
//            ByteArrayOutputStream fakeOutputStream = new ByteArrayOutputStream();
//            when(mockFactory.createConnection(tokenEndpoint, "POST", "application/x-www-form-urlencoded"))
//                    .thenReturn(mockedConnection);
//            when(mockedConnection.getOutputStream()).thenReturn(fakeOutputStream);
//            when(mockedConnection.getResponseCode()).thenReturn(401);
//            // When
//            String actual = oauth2AuthorizationService.buildAuthorizationAccessURL(
//                    clientId, clientSecret, redirectUri,code, codeVerifier);
//            HttpURLConnection result = oauth2AuthorizationService.getUrlConnection(
//                    tokenEndpoint,
//                    actual,
//                    "POST",
//                    "application/x-www-form-urlencoded"
//            );
//            assertNotNull(mockedConnection);
//            when(mockFactory.createConnection(tokenEndpoint,"POST",
//                    "application/x-www-form-urlencoded")).thenReturn(mockedConnection);
//
//            when(mockedConnection.getOutputStream()).thenReturn(fakeOutputStream);
//            when(mockedConnection.getResponseCode()).thenReturn(401);
//            // Then
//            assertNotNull(actual);
//            assertEquals(expectedUrlParameters, actual);
//            assertEquals(401, result.getResponseCode());
//
//            verify(mockedConnection).setDoOutput(true);
//            verify(mockedConnection).setRequestMethod("POST");
//            verify(mockedConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            verify(mockedConnection).setConnectTimeout(CONNECTION_TIMEOUT);
//            verify(mockedConnection).setReadTimeout(READ_TIMEOUT);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void voidTest() throws Exception {
        System.out.println("Test is not implemented yet.");
    }
}