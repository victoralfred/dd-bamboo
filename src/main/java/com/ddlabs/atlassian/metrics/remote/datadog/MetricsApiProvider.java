package com.ddlabs.atlassian.metrics.remote.datadog;

import com.ddlabs.atlassian.api.HttpConnectionFactory;
import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.datadog.api.client.ApiClient;
import com.datadog.api.client.v1.api.MetricsApi;
import com.ddlabs.atlassian.metrics.model.MSConfig;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.metrics.remote.MetricServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.Optional;

@Component
public class MetricsApiProvider {
    private static final Logger log = LoggerFactory.getLogger(MetricsApiProvider.class);

    private final OAuth2AuthorizationService authService;
    private final MetricServerFactory serverFactory;
    private final PluginDaoRepository pluginDao;
    private final ApiClient apiClient;

    public MetricsApiProvider(OAuth2AuthorizationService authService,
                              MetricServerFactory serverFactory,
                              PluginDaoRepository pluginDao,
                              ApiClient apiClient) {
        this.authService = authService;
        this.serverFactory = serverFactory;
        this.pluginDao = pluginDao;
        this.apiClient = apiClient;
    }
    public MetricsApi getV1Api(String provider) {
        configureAccessToken(provider);
        return new MetricsApi(apiClient);
    }
    public com.datadog.api.client.v2.api.MetricsApi getV2Api(String provider) {
        configureAccessToken(provider);
        return new com.datadog.api.client.v2.api.MetricsApi(apiClient);
    }
    private void configureAccessToken(String provider) {
        MetricServer metricServer = serverFactory.getMetricServer(provider);
        try {
            MSConfig token = Optional.ofNullable(pluginDao.getServerConfigByType(provider))
                    .orElseThrow(() -> new IllegalArgumentException("Missing config for provider: " + provider));
            if(authService.isAccessTokenExpired(token.getAccessTokenExpiry())){
                authService.refreshToken(metricServer);
            }
        } catch (Exception e) {
            log.error("Failed to configure API client for provider {}: ", provider, e);
            throw new RuntimeException(e);
        }
    }

}