package com.ddlabs.atlassian.impl.config;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.ddlabs.atlassian.impl.cache.MetricServerConfigurationCache;
import com.ddlabs.atlassian.impl.config.model.AvailableServers;
import com.ddlabs.atlassian.impl.data.adapter.dto.AvailableServerDTO;
import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.data.adapter.entity.ServerConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class MetricServerView extends HttpServlet {
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserService userService;
    private final MetricServerConfigurationCache metricServerConfigurationCache;
    private final AvailableServerDTO availableServerDTO;
    private final Logger log = LoggerFactory.getLogger(MetricServerView.class);
    public MetricServerView(TemplateRenderer templateRenderer, UserService userService,
                            MetricServerConfigurationCache metricServerConfigurationCache,
                            AvailableServerDTO availableServerDTO) {
        super();
        this.templateRenderer = templateRenderer;
        this.userService = userService;
        this.metricServerConfigurationCache = metricServerConfigurationCache;
        this.availableServerDTO = availableServerDTO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        userService.authCheck(req, resp);
        Map<String, Object> params =  new HashMap<>();
        final List<AvailableServers> serverConfigs = metricServerConfigurationCache.getAllServerConfigs()
                .stream().filter(ServerConfigBuilder::nonNull)
                        .map(this::replaceAllEmptyOrNullValueWithEmpty)
                                .toList();
        resp.setContentType("text/html; charset=UTF-8");
        params.put("servers", serverConfigs);
        params.put("isAdmin", userService.isAuthenticatedUserAndAdmin());
        try {
            templateRenderer.render("/templates/metrics-server-view.vm", params, resp.getWriter());
        } catch (NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    private AvailableServers replaceAllEmptyOrNullValueWithEmpty(ServerConfigBuilder result) {
        return availableServerDTO.apply(result);
    }
}
