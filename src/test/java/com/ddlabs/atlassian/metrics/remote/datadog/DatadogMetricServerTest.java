package com.ddlabs.atlassian.metrics.remote.datadog;

import com.ddlabs.atlassian.auth.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.auth.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.auth.oauth2.service.OAuth2Service;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.config.model.ServerBodyBuilder;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.data.mapper.ServerConfigMapper;
import com.ddlabs.atlassian.data.repository.ServerConfigRepository;
import com.ddlabs.atlassian.exception.ConfigurationException;
import com.ddlabs.atlassian.exception.DataAccessException;
import com.ddlabs.atlassian.exception.ValidationException;
import com.ddlabs.atlassian.metrics.api.factory.MetricsApiClientFactory;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.util.ValidationUtils;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
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
    private ServerBodyBuilder serverBodyBuilder;
    @Mock
    private MetricsApiClientFactory metricsApiClientFactory;
    private DatadogMetricServer datadogMetricServer;
    @Mock
    private MSConfigEntity config;
    HttpServletRequest req;
    ServerConfigDTO serverConfigDTO;
    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        datadogMetricServer = new DatadogMetricServer(
                serverConfigMapper,
                oauth2Service,
                userService,
                serverConfigRepository,
                serverBodyBuilder,
                metricsApiClientFactory
        );
        config = mock(MSConfigEntity.class);
        req = mock(HttpServletRequest.class);
        serverConfigDTO = mock(ServerConfigDTO.class);
        assertNotNull(datadogMetricServer);
    }

    public void testSetupOauth2Authentication_Success() throws Exception {
        String serverName = "testServer";
        when(serverConfigRepository.findByServerType(serverName)).thenReturn(config);
        when(config.getCodeChallenge()).thenReturn("encryptedCodeChallenge");
        when(config.getApiEndpoint()).thenReturn("https://api.example.com");
        when(config.getOauthEndpoint()).thenReturn("https://auth.example.com");
        when(config.getClientId()).thenReturn("clientId123");
        when(config.getRedirectUrl()).thenReturn("https://redirect.example.com");
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
        config.setClientSecret("encryptedSecret");
        config.setRedirectUrl("https://redirect.url");
        config.setCodeVerifier("encryptedVerifier");
        // Mock input params
        when(req.getParameter("code")).thenReturn(code);
        when(req.getParameter("client_id")).thenReturn(clientId);
        when(serverConfigRepository.findByServerType(serverName)).thenReturn(config);
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
        serverConfigDTO.setClientSecret("encryptedSecret");
        serverConfigDTO.setRedirectUrl("https://redirect.url");
        serverConfigDTO.setCodeVerifier("encryptedVerifier");
        when(serverConfigRepository.findByServerType(serverName)).thenReturn(config);
        verify(serverConfigRepository, never()).save(any(ServerConfigDTO.class));
    }

    public void testGetConfigDefaults() {
    }

    public void testSaveServer() {
    }

    public void testGetMetricsApiClient() {
    }
}