package com.ddlabs.atlassian.remote;


import com.ddlabs.atlassian.model.ConfigDefaults;
import com.ddlabs.atlassian.model.ServerConfigBody;
import com.ddlabs.atlassian.util.exceptions.NullOrEmptyFieldsException;

import javax.servlet.http.HttpServletRequest;

public interface MetricServer {
    /**
     * This method sets up OAuth2 authentication for the specified server.
     * It prepares the necessary parameters and redirects the user to the OAuth2 authorization endpoint.
     *
     * @param serverName The name of the server for which to set up OAuth2 authentication.
     * @return A String containing the redirect URL for OAuth2 authentication.
     * @throws NullOrEmptyFieldsException if the serverName is null or empty, or if any required fields are missing.
     */
    String setupOauth2Authentication(String serverName) throws NullOrEmptyFieldsException;
    /**
     * This method retrieves the access token for the specified server.
     * It uses the OAuth2 authorization service to exchange the authorization code for an access token.
     *
     * @param req        The HTTP request containing parameters needed for token retrieval.
     * @param serverName The name of the server for which to retrieve the access token.
     * @return The access token as a String, or null if retrieval fails.
     * @throws NullOrEmptyFieldsException if the serverName is null or empty, or if any required fields are missing.
     */
    String getAccessToken(HttpServletRequest req, String serverName) throws NullOrEmptyFieldsException;
    /**
     * Saves the server configuration to the database.
     *
     * @param serverConfig The server configuration to save.
     * @return A message indicating the result of the save operation.
     * @throws NullOrEmptyFieldsException if any required fields in the serverConfig are null or empty.
     */
    String saveServer(ServerConfigBody serverConfig) throws NullOrEmptyFieldsException;
    /**
     * Saves the server metadata after retrieving the access token.
     *
     * @param serverType The type of the server (e.g., Datadog, NewRelic).
     * @param response   The response containing the access token and other metadata.
     * @param req        The HTTP request containing additional parameters if needed.
     * @return A message indicating the result of the save operation.
     * @throws NullOrEmptyFieldsException if any required fields are null or empty.
     */
    String saveServerMetadata(String  serverType, String response, HttpServletRequest req) throws NullOrEmptyFieldsException;
    /**
     * Retrieves the default configuration settings for the metric server.
     *
     * @return An instance of ConfigDefaults containing the default configuration values.
     */
    ConfigDefaults getConfigDefaults();
}
