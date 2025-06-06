package com.ddlabs.atlassian.auth.oauth2.service;

import com.ddlabs.atlassian.impl.config.UserService;
import com.ddlabs.atlassian.impl.data.adapter.entity.ServerConfigRepository;
import com.ddlabs.atlassian.oauth2.OAuth2AuthorizationServiceImpl;
import com.ddlabs.atlassian.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.impl.exception.AuthenticationException;
import com.ddlabs.atlassian.api.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DefaultOAuth2ServiceTest {
    private OAuth2AuthorizationServiceImpl oauth2Service;
    @Mock
    private HttpClient httpClient;
    @Mock
    private  ServerConfigRepository serverConfigRepository;
    private  UserService userService;
    @Mock
    private OAuth2Configuration testConfig;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        oauth2Service = new OAuth2AuthorizationServiceImpl(httpClient,
                serverConfigRepository, userService);
        
        testConfig = new OAuth2Configuration();
        testConfig.setClientId("test-client-id");
        testConfig.setClientSecret("test-client-secret");
        testConfig.setRedirectUri("https://test.com/callback");
        testConfig.setAuthEndpoint("https://test.com/oauth2/authorize");
        testConfig.setTokenEndpoint("https://test.com/oauth2/token");
        testConfig.setCodeChallenge("test-code-challenge");
        testConfig.setCodeChallengeMethod("S256");
        testConfig.setCodeVerifier("test-code-verifier");
    }
    @Test
    public void testGenerateAuthorizationUrl() throws AuthenticationException {
        String authUrl = oauth2Service.generateAuthorizationUrl(testConfig);
        assertNotNull(authUrl);
        assertTrue(authUrl.startsWith(testConfig.getAuthEndpoint()));
        assertTrue(authUrl.contains("redirect_uri=" +  URLEncoder.encode(testConfig.getRedirectUri(), StandardCharsets.UTF_8)));
        assertTrue(authUrl.contains("&client_id=" + URLEncoder.encode(testConfig.getClientId(), StandardCharsets.UTF_8)));
        assertTrue(authUrl.contains("&response_type=code"));
        assertTrue(authUrl.contains("&code_challenge=" + URLEncoder.encode(testConfig.getCodeChallenge(), StandardCharsets.UTF_8)));
        assertTrue(authUrl.contains("&code_challenge_method=" + URLEncoder.encode(testConfig.getCodeChallengeMethod(), StandardCharsets.UTF_8)));
    }

    @Test

    public void testExchangeCodeForTokens() throws Exception {
        String tokenResponse = "{"
                + "\"access_token\": \"test-access-token\","
                + "\"refresh_token\": \"test-refresh-token\","
                + "\"token_type\": \"bearer\","
                + "\"scope\": \"read write\","
                + "\"expires_in\": 3600"
                + "}";
        
        when(httpClient.post(eq(testConfig.getTokenEndpoint()), anyString(), eq("application/x-www-form-urlencoded")))
                .thenReturn(tokenResponse);
        
        OAuth2TokenResponse response = oauth2Service.exchangeCodeForTokens("test-code", testConfig);
        
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());
        assertEquals("bearer", response.getTokenType());
        assertEquals("read write", response.getScope());
        assertEquals(3600, response.getExpiresIn());
    }

    @Test
    public void testIsAccessTokenExpired() {
        // Token expired 1 second ago
        long expiredTime = Instant.now().minusSeconds(1).getEpochSecond();
        assertTrue(oauth2Service.isAccessTokenExpired(expiredTime));
        
        // Token expires 1 hour from now
        long validTime = Instant.now().plusSeconds(3600).getEpochSecond();
        assertFalse(oauth2Service.isAccessTokenExpired(validTime));
    }
    
    @Test
    public void testIsTokenExpired() {
        OAuth2TokenResponse expiredToken = new OAuth2TokenResponse();
        expiredToken.setAccessTokenExpiry(Instant.now().minusSeconds(1).getEpochSecond());
        assertTrue(oauth2Service.isTokenExpired(expiredToken));
        
        OAuth2TokenResponse validToken = new OAuth2TokenResponse();
        validToken.setAccessTokenExpiry(Instant.now().plusSeconds(3600).getEpochSecond());
        assertFalse(oauth2Service.isTokenExpired(validToken));
    }
}