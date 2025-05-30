package com.ddlabs.atlassian.config;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.metrics.model.ConfiguredMetricServers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricServerView extends HttpServlet {
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserService userService;
    private final PluginDaoRepository pluginDaoRepository;
    private final Logger log = LoggerFactory.getLogger(MetricServerView.class);

    public MetricServerView(TemplateRenderer templateRenderer, UserService userService, PluginDaoRepository pluginDaoRepository) {
        super();
        this.templateRenderer = templateRenderer;
        this.userService = userService;
        this.pluginDaoRepository = pluginDaoRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        userService.authCheck(req, resp);
        Map<String, Object> params =  new HashMap<>();
        List<ConfiguredMetricServers> serverConfigs = pluginDaoRepository.getAllServerConfigs();
        resp.setContentType("text/html; charset=UTF-8");
        params.put("servers", serverConfigs);
        if(log.isDebugEnabled()) {
            serverConfigs.forEach(configuredMetricServer -> log.debug("Configured Metric Server: {}, Online: {}, Authentication: {}, Type: {}, Configured: {}, Enabled: {}, Description: {}",
                    configuredMetricServer.getServerName(),
                    configuredMetricServer.isOnline(),
                    configuredMetricServer.isAuthentication(),
                    configuredMetricServer.getServerType(),
                    configuredMetricServer.isConfigured(),
                    configuredMetricServer.isEnabled(),
                    configuredMetricServer.getDescription()));
        }

        params.put("isAdmin", userService.isAuthenticatedUserAndAdmin());
        log.info("Rendering metric server configuration page with params: {}", params.get("servers"));
        try {
            templateRenderer.render("/templates/metrics-server-view.vm", params, resp.getWriter());
        } catch (NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
