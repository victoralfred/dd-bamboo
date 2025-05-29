package com.ddlabs.atlassian.metrics.remote.datadog;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.metrics.remote.MetricServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class MetricsApiProvider {
    private static final Logger log = LoggerFactory.getLogger(MetricsApiProvider.class);

    private final OAuth2AuthorizationService authService;
    private final MetricServerFactory serverFactory;
    private final PluginDaoRepository pluginDao;

    public MetricsApiProvider(OAuth2AuthorizationService authService,
                              MetricServerFactory serverFactory,
                              PluginDaoRepository pluginDao) {
        this.authService = authService;
        this.serverFactory = serverFactory;
        this.pluginDao = pluginDao;
    }

    public String configureAccessToken(String provider) {
        MetricServer metricServer = serverFactory.getMetricServer(provider);
        try {
            MSConfigEntity token = Optional.ofNullable(pluginDao.getServerConfigByType(provider))
                    .orElseThrow(() -> new IllegalArgumentException("Missing config for provider: " + provider));
            if(authService.isAccessTokenExpired(token.getAccessTokenExpiry())){
                log.info("Token is expired. Creating new token");
                authService.refreshToken(metricServer);
                log.info("Token is refreshed. Creating new token");
                return token.getAccessToken();
            }else {
                return token.getAccessToken();
            }
        } catch (Exception e) {
            log.error("Failed to configure API client for provider {}: ", provider, e);
            throw new RuntimeException(e);
        }
    }

}