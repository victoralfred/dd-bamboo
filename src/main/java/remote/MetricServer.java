package remote;


import com.ddlabs.atlassian.model.ServerConfigProperties;
import javax.servlet.http.HttpServletRequest;

public interface MetricServer {
    String setupOauth2Authentication(String serverName);
    String getAccessToken(final HttpServletRequest req, String serverName);
    String saveServer(ServerConfigProperties serverConfig);
    String saveServerMetadata(String  serverType, String response);
}
