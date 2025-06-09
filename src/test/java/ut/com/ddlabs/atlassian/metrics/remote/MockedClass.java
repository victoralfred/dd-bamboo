package ut.com.ddlabs.atlassian.metrics.remote;


import com.ddlabs.atlassian.api.MetricServer;
import com.ddlabs.atlassian.impl.config.model.ConfigDefaults;
import com.ddlabs.atlassian.impl.config.model.ServerConfigBody;
import com.ddlabs.atlassian.impl.exception.NullOrEmptyFieldsException;

import javax.servlet.http.HttpServletRequest;
public class MockedClass implements MetricServer {
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
    public void saveServerMetadata(String serverType, String response, HttpServletRequest req) throws NullOrEmptyFieldsException {
    }

    @Override
    public ConfigDefaults getConfigDefaults() {
        return null;
    }

    @Override
    public String deleteServer(String serverName) {
        return "";
    }
}
