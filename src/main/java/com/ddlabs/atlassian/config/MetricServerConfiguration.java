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
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricServerConfiguration extends HttpServlet {
    private final Logger log = LoggerFactory.getLogger(MetricServerConfiguration.class);
    @Serial
    private static final long serialVersionUID = 89800000L;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserService userService;
    private final PluginDaoRepository pluginDaoRepository;
    public MetricServerConfiguration(TemplateRenderer templateRenderer,
                                     UserService userService, PluginDaoRepository pluginDaoRepository) {
        super();
        this.templateRenderer = templateRenderer;
        this.userService = userService;
        this.pluginDaoRepository = pluginDaoRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (!userService.isAuthenticatedUserAndAdmin()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            userService.redirectToLoginPage(req, resp);
            return;
        }
        Map<String, Object> params =  new HashMap<>();
        List<ConfiguredMetricServers> serverConfigs = pluginDaoRepository.getAllServerConfigs();
        resp.setContentType("text/html; charset=UTF-8");
        params.put("servers", serverConfigs);

        params.put("isAdmin", userService.isAuthenticatedUserAndAdmin());
        log.info("Rendering metric server configuration page with params: {}", params.get("servers"));
        try {
            templateRenderer.render("/templates/metric-server-config.vm", params, resp.getWriter());
        } catch (NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
