package com.ddlabs.atlassian.config;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

public class MetricServerConfiguration extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 89800000L;
    @ComponentImport
    private final UserManager userManager;

    public MetricServerConfiguration(UserManager userManager) {
        super();
        this.userManager = userManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

}
