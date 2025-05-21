package com.ddlabs.atlassian.config;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.ddlabs.atlassian.model.ConfiguredMetricServers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MetricServerConfiguration extends HttpServlet {
    private static final long serialVersionUID = 89800000L;
    private static final String PLUGIN_STORAGE_KEY = "com.ddlabs.atlassian.dd-bamboo-metrics";
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserService userService;
    public MetricServerConfiguration(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer templateRenderer,
                                     UserService userService) {
        super();
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.templateRenderer = templateRenderer;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!userService.isAuthenticatedUserAndAdmin()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            userService.redirectToLoginPage(req, resp);
            return;
        }
        Map<String, Object> params =  new HashMap<>();
        resp.setContentType("text/html; charset=UTF-8");

        params.put("servers", Arrays.asList(
                new ConfiguredMetricServers("Server 1", true, true),
                new ConfiguredMetricServers("Server 2", false, false),
                new ConfiguredMetricServers("Server 3", true, false)
        ));

        params.put("isAdmin", userService.isAuthenticatedUserAndAdmin());
        try {
            templateRenderer.render("/templates/metric-server-config.vm", params, resp.getWriter());
        } catch (NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".serverName", req.getParameter("serverName"));
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".serverUrl", req.getParameter("serverUrl"));
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".description", req.getParameter("description"));
        pluginSettings.put(PLUGIN_STORAGE_KEY + "clientKey", userService.encrypt(req.getParameter("clientKey")));
        pluginSettings.put(PLUGIN_STORAGE_KEY + "clientSecret", userService.encrypt(req.getParameter("clientSecret")));
        resp.sendRedirect(req.getContextPath() + "/plugins/servlet/metrics");
    }
}
