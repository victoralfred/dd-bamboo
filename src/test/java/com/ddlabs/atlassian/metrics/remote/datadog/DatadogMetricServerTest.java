package com.ddlabs.atlassian.metrics.remote.datadog;

import com.ddlabs.atlassian.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.api.OAuth2Service;
import com.ddlabs.atlassian.impl.config.UserService;
import com.ddlabs.atlassian.impl.config.model.ServerBodyBuilder;
import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigMapper;
import com.ddlabs.atlassian.impl.data.adapter.entity.ServerConfigRepository;
import com.ddlabs.atlassian.impl.metrics.api.factory.MetricsApiClientFactory;
import com.ddlabs.atlassian.impl.metrics.remote.datadog.DatadogMetricServer;
import junit.framework.TestCase;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DatadogMetricServerTest extends TestCase {
    @Mock
    private ServerConfigMapper serverConfigMapper;
    @Mock
    private OAuth2Service oauth2Service;
    @Mock
    private UserService userService;
    @Mock
    private ServerConfigRepository serverConfigRepository;
    @Mock
    private MetricsApiClientFactory metricsApiClientFactory;
    private DatadogMetricServer datadogMetricServer;
    @Mock
    ServerBodyBuilder serverBodyBuilder;
    HttpServletRequest req;
    @Mock
    ServerConfigBuilder serverConfigBuilder;
    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        serverConfigBuilder = mock(ServerConfigBuilder.class);
        datadogMetricServer = new DatadogMetricServer(
                serverConfigMapper,
                oauth2Service,
                userService,
                serverConfigRepository,
                serverBodyBuilder,
                metricsApiClientFactory
        );
        req = mock(HttpServletRequest.class);

        assertNotNull(datadogMetricServer);
    }

    public void testSetupOauth2Authentication_Success() throws Exception {
        String serverName = "testServer";
        when(serverConfigRepository.findByServerType(serverName)).thenReturn(serverConfigBuilder);
        when(serverConfigBuilder.getCodeChallenge()).thenReturn("encryptedCodeChallenge");
        when(serverConfigBuilder.getApiEndpoint()).thenReturn("https://api.example.com");
        when(serverConfigBuilder.getOauthEndpoint()).thenReturn("https://auth.example.com");
        when(serverConfigBuilder.getClientId()).thenReturn("clientId123");
        when(serverConfigBuilder.getRedirectUrl()).thenReturn("https://redirect.example.com");
        // Mock userService.decrypt
        when(userService.decrypt("encryptedCodeChallenge")).thenReturn("decryptedCodeChallenge");
        // Mock oauth2Service.generateAuthorizationUrl
        when(oauth2Service.generateAuthorizationUrl(any(OAuth2Configuration.class)))
                .thenReturn("https://authorization.url");
        // Call the method
        String result = datadogMetricServer.setupOauth2Authentication(serverName);
        // Verify
        assertEquals("https://authorization.url", result);
        // Optionally verify interactions
        verify(serverConfigRepository).findByServerType(serverName);
        verify(userService).decrypt("encryptedCodeChallenge");
        verify(oauth2Service).generateAuthorizationUrl(any(OAuth2Configuration.class));
    }
    public void testGetAccessToken() {
        String serverName = "testServer";
        String code = "authCode";
        String clientId = "clientId123";
        serverConfigBuilder.setClientSecret("encryptedSecret");
        serverConfigBuilder.setRedirectUrl("https://redirect.url");
        serverConfigBuilder.setCodeVerifier("encryptedVerifier");
        // Mock input params
        when(req.getParameter("code")).thenReturn(code);
        when(req.getParameter("client_id")).thenReturn(clientId);
        when(serverConfigRepository.findByServerType(serverName)).thenReturn(serverConfigBuilder);
        when(userService.decrypt("encryptedSecret")).thenReturn("decryptedSecret");
        when(userService.decrypt("encryptedVerifier")).thenReturn("decryptedVerifier");
        // Prepare OAuth2 config and token response
        OAuth2TokenResponse tokenResponse = new OAuth2TokenResponse();
        tokenResponse.setAccessToken("accessToken123");
        tokenResponse.setRefreshToken("refreshToken123");
        tokenResponse.setTokenType("Bearer");
        tokenResponse.setScope("read write");
        tokenResponse.setExpiresIn(3600);

        when(oauth2Service.exchangeCodeForTokens(eq(code), any(OAuth2Configuration.class)))
                .thenReturn(tokenResponse);
        // Call method under test
        String result = datadogMetricServer.getAccessToken(req, serverName);
        // Assert output contains expected token values as JSON string
        assertTrue(result.contains("\"access_token\":\"accessToken123\""));
        assertTrue(result.contains("\"refresh_token\":\"refreshToken123\""));
        assertTrue(result.contains("\"token_type\":\"Bearer\""));
        assertTrue(result.contains("\"scope\":\"read write\""));
        assertTrue(result.contains("\"expires_in\":3600"));
    }

    public void testSaveServerMetadata() {
        String serverName = "testServer";
        serverConfigBuilder.setClientSecret("encryptedSecret");
        serverConfigBuilder.setRedirectUrl("https://redirect.url");
        serverConfigBuilder.setCodeVerifier("encryptedVerifier");
        when(serverConfigRepository.findByServerType(serverName)).thenReturn(serverConfigBuilder);
        verify(serverConfigRepository, never()).save(any(ServerConfigBuilder.class));
    }

    public void testGetConfigDefaults() {
    }

    public void testSaveServer() {
    }

    public void testGetMetricsApiClient() {
    }
}