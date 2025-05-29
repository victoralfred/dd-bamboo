package com.ddlabs.atlassian.auth.oauth2.service;

import com.ddlabs.atlassian.auth.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.auth.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.exception.AuthenticationException;
import com.ddlabs.atlassian.http.HttpClient;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DefaultOAuth2ServiceTest {

    private DefaultOAuth2Service oauth2Service;
    
    @Mock
    private HttpClient httpClient;
    
    private OAuth2Configuration testConfig;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        oauth2Service = new DefaultOAuth2Service(httpClient);
        
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
    

    public void testGenerateAuthorizationUrl() throws AuthenticationException {
        String authUrl = oauth2Service.generateAuthorizationUrl(testConfig);
        
        assertNotNull(authUrl);
        assertTrue(authUrl.startsWith(testConfig.getAuthEndpoint()));
        assertTrue(authUrl.contains("client_id=" + testConfig.getClientId()));
        assertTrue(authUrl.contains("redirect_uri=" + testConfig.getRedirectUri()));
        assertTrue(authUrl.contains("response_type=code"));
        assertTrue(authUrl.contains("code_challenge=" + testConfig.getCodeChallenge()));
        assertTrue(authUrl.contains("code_challenge_method=" + testConfig.getCodeChallengeMethod()));
    }
    

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
    


    

    public void testIsAccessTokenExpired() {
        // Token expired 1 second ago
        long expiredTime = Instant.now().minusSeconds(1).getEpochSecond();
        assertTrue(oauth2Service.isAccessTokenExpired(expiredTime));
        
        // Token expires 1 hour from now
        long validTime = Instant.now().plusSeconds(3600).getEpochSecond();
        assertFalse(oauth2Service.isAccessTokenExpired(validTime));
    }
    

    public void testIsTokenExpired() {
        OAuth2TokenResponse expiredToken = new OAuth2TokenResponse();
        expiredToken.setAccessTokenExpiry(Instant.now().minusSeconds(1).getEpochSecond());
        assertTrue(oauth2Service.isTokenExpired(expiredToken));
        
        OAuth2TokenResponse validToken = new OAuth2TokenResponse();
        validToken.setAccessTokenExpiry(Instant.now().plusSeconds(3600).getEpochSecond());
        assertFalse(oauth2Service.isTokenExpired(validToken));
    }
}