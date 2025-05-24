package remote;


import com.ddlabs.atlassian.model.ConfigDefaults;
import com.ddlabs.atlassian.model.ServerConfigBody;
import javax.servlet.http.HttpServletRequest;

public interface MetricServer {
    /**
     * This method sets up the OAuth2 authentication URL for Datadog.
     * It retrieves the server configuration from the pluginDao and builds the authorization URL.
     *
     * @param serverName The name of the server for which to set up OAuth2 authentication.
     * @return The OAuth2 authorization URL as a String.
     */
    String setupOauth2Authentication(String serverName);
    /**
     * This method retrieves the access token for the specified server.
     * It uses the OAuth2 authorization service to exchange the authorization code for an access token.
     *
     * @param req        The HTTP request containing parameters needed for token retrieval.
     * @param serverName The name of the server for which to retrieve the access token.
     * @return The access token as a String, or null if retrieval fails.
     */
    String getAccessToken(HttpServletRequest req, String serverName);
    String saveServer(ServerConfigBody serverConfig);
    /**
     * Saves the server metadata after successful OAuth2 authentication.
     *
     * @param serverType The type of the server (e.g., "datadog").
     * @param response   The response from the OAuth2 token endpoint.
     * @param req        The HTTP request containing additional parameters.
     * @return A string indicating success or error.
     */
    String saveServerMetadata(String  serverType, String response, HttpServletRequest req);
    /**
     * Retrieves the default configuration settings for the metric server.
     *
     * @return An instance of ConfigDefaults containing the default configuration values.
     */
    ConfigDefaults getConfigDefaults();
}
