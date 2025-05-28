package com.ddlabs.atlassian.metrics.rest;


import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.metrics.model.ServerConfigBody;
import com.ddlabs.atlassian.metrics.model.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.metrics.remote.MetricServerFactory;
import com.ddlabs.atlassian.metrics.remote.datadog.DatadogMetricServer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class Oauth2ApiClient {
    private static final Logger log = LoggerFactory.getLogger(Oauth2ApiClient.class);
    private final MetricServerFactory metricServerFactory;
    private final UserService userService;
    @Inject
    public Oauth2ApiClient(MetricServerFactory metricServerFactory,
                           UserService userService) {
        this.metricServerFactory = metricServerFactory;
        this.userService = userService;
    }
    @GET
    @Path("authorize/{serverType}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response request_authorization(@PathParam("serverType") String serverType, @Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(serverType);
            final String oauth_request_url =  metricServer.setupOauth2Authentication(serverType);
            return oauth_request_url!=null?Response.ok(oauth_request_url).build(): Response.serverError().build();

    }
    @GET
    @Path("token")
    public Response request_accessToken(@Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(extractServerType(req.getParameter("domain")));
        String access_token_response = metricServer.getAccessToken(req, extractServerType(req.getParameter("domain")));
        String response = metricServer.saveServerMetadata(extractServerType(req.getParameter("domain")),
                access_token_response,req);
        log.info("Server response {}", response);
        return Response.temporaryRedirect(URI.create("http://localhost:6990/bamboo/plugins/servlet/metrics")).build();
    }
    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveServer(ServerConfigBody serverConfig,
                               @Context HttpServletRequest req){
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(serverConfig.getServerType());
        String savedStatus = metricServer.saveServer(serverConfig);
        return Response.ok(savedStatus).build();
    }
    @POST
    @Path("discover")
    @Produces(MediaType.APPLICATION_JSON)
    public Response discoverServers(ServerType serverType, @Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(serverType.getServerType());
        return Response.ok(metricServer.getConfigDefaults()).build();
    }
    /**
     * Extracts the server type from the provided server URL.
     * The server type is determined by the first part of the URL before the first dot.
     *
     * @param serverUrl the server URL to extract the type from
     * @return the extracted server type, or null if it cannot be determined
     */
    private String extractServerType(String serverUrl) {
        if (serverUrl == null || serverUrl.isEmpty()) {
            return null;
        }
        String[] parts = serverUrl.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        return extractClientId(parts[0]);
    }
    /**
     * Extracts the client ID based on the server type.
     * Currently, it only supports "datadoghq" as a valid server type.
     *
     * @param type the server type to extract the client ID from
     * @return the client ID if the type is "datadoghq", otherwise null
     */
    private String extractClientId(String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        if ("datadoghq".equalsIgnoreCase(type)) {
            return DatadogMetricServer.class.getSimpleName().trim();
        }
        return null;
    }
}
