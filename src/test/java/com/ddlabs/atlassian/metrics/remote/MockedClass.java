package com.ddlabs.atlassian.metrics.remote;

import com.ddlabs.atlassian.metrics.model.ConfigDefaults;
import com.ddlabs.atlassian.metrics.model.ServerConfigBody;
import com.ddlabs.atlassian.util.exceptions.NullOrEmptyFieldsException;

import javax.servlet.http.HttpServletRequest;
public class MockedClass implements MetricServer{
    @Override
    public String setupOauth2Authentication(String serverName) throws NullOrEmptyFieldsException {
        return "";
    }

    @Override
    public String getAccessToken(HttpServletRequest req, String serverName) throws NullOrEmptyFieldsException {
        return "";
    }

    @Override
    public String saveServer(ServerConfigBody serverConfig) throws NullOrEmptyFieldsException {
        return "";
    }

    @Override
    public String saveServerMetadata(String serverType, String response, HttpServletRequest req) throws NullOrEmptyFieldsException {
        return "";
    }

    @Override
    public ConfigDefaults getConfigDefaults() {
        return null;
    }
}
