package com.ddlabs.atlassian.metrics.remote.datadog;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.metrics.remote.MetricServerFactory;
import com.ddlabs.atlassian.metrics.remote.MetricServerFactoryImpl;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class MetricsApiProviderTest extends TestCase {
    @Mock
    private OAuth2AuthorizationService authService;
    @Mock
    private MetricServerFactory serverFactory;
    @Mock
    private PluginDaoRepository pluginDao;
    @Mock
    private DatadogMetricServer datadogMetricServer;
    @Mock
    private MSConfigEntity msConfigEntity;
    private MetricsApiProvider metricsApiProvider;


    public void setUp() {
        MockitoAnnotations.openMocks(this);
        metricsApiProvider = new MetricsApiProvider(authService, serverFactory, pluginDao);
        msConfigEntity.setAccessToken("TestAccessToken");
    }
    public void testConfigureAccessToken() {
        String provider = "DatadogMetricServer";
        try {
            when(serverFactory.getMetricServer(provider)).thenReturn(datadogMetricServer);
            when(pluginDao.getServerConfigByType(provider)).thenReturn(msConfigEntity);
            when(authService.isAccessTokenExpired(msConfigEntity.getAccessTokenExpiry())).thenReturn(false);
            when(msConfigEntity.getAccessToken()).thenReturn("TestAccessToken");
            String accessToken = metricsApiProvider.configureAccessToken(provider);
            assertNotNull(accessToken);
            assertEquals("TestAccessToken", accessToken);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

    }
}