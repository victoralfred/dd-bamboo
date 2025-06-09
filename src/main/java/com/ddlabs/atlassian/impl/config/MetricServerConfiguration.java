package com.ddlabs.atlassian.impl.config;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class MetricServerConfiguration extends HttpServlet {
    private final Logger log = LoggerFactory.getLogger(MetricServerConfiguration.class);
    @Serial
    private static final long serialVersionUID = 89800000L;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserService userService;
    public MetricServerConfiguration(TemplateRenderer templateRenderer,UserService userService) {
        super();
        this.templateRenderer = templateRenderer;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        userService.authCheck(req, resp);
        Map<String, Object> params =  new HashMap<>();
        resp.setContentType("text/html; charset=UTF-8");
        params.put("isAdmin", userService.isAuthenticatedUserAndAdmin());
        try {
            templateRenderer.render("/templates/metric-server-config.vm", params, resp.getWriter());
        } catch (NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

