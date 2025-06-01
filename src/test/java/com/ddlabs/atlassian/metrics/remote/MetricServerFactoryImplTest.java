package com.ddlabs.atlassian.metrics.remote;

import com.ddlabs.atlassian.auth.oauth2.service.OAuth2Service;
import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.data.mapper.ServerConfigMapper;
import com.ddlabs.atlassian.data.repository.ServerConfigRepository;
import com.ddlabs.atlassian.metrics.api.factory.MetricsApiClientFactory;
import com.ddlabs.atlassian.metrics.model.ServerBodyBuilder;
import com.ddlabs.atlassian.metrics.remote.datadog.DatadogMetricServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MetricServerFactoryImplTest {
    private Map<String, MetricServer> metricServers = new HashMap<>();
    @Mock
    private  ServerConfigMapper serverConfigMapper;
    @Mock
    private  OAuth2Service oauth2Service;
    @Mock
    private  UserService userService;
    @Mock
    private  ServerConfigRepository serverConfigRepository;
    @Mock
    private  ServerBodyBuilder serverBodyBuilder;
    @Mock
    private  MetricsApiClientFactory metricsApiClientFactory;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        metricServers.put("DatadogMetricServer", new DatadogMetricServer(
                serverConfigMapper,oauth2Service,userService,serverConfigRepository,
                serverBodyBuilder, metricsApiClientFactory
        ));
        metricServers.put("MockedClass", new MockedClass());
    }
    @Test
    void itShouldGetMetricServer() {
        // Given
        MetricServerFactory metricServerFactory = new MetricServerFactoryImpl(metricServers);
        // When
        String provider = DatadogMetricServer.class.getSimpleName();
        // Then
        MetricServer metricServer = metricServerFactory.getMetricServer(provider);
        assertNotNull(metricServer);
        assertInstanceOf(DatadogMetricServer.class, metricServer);
    }
    @Test
    void itShouldGetMockedClass(){
        //Given
        MetricServerFactory metricServerFactory = new MetricServerFactoryImpl(metricServers);
        //When
        String provider = MockedClass.class.getSimpleName();
        MetricServer metricServer = metricServerFactory.getMetricServer(provider);
        assertNotNull(metricServer);
        assertInstanceOf(MockedClass.class, metricServer);
    }
}