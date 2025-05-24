package com.ddlabs.atlassian.auth;


import com.ddlabs.atlassian.config.UserService;
import com.ddlabs.atlassian.model.ServerConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remote.MetricServer;
import remote.MetricServerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class OauthClientServlet {
    private static final Logger log = LoggerFactory.getLogger(OauthClientServlet.class);
    private final MetricServerFactory metricServerFactory;
    private final UserService userService;
    @Inject
    public OauthClientServlet(MetricServerFactory metricServerFactory,
                              UserService userService) {
        this.metricServerFactory = metricServerFactory;
        this.userService = userService;
    }
    @POST
    @Path("authorize")
    @Produces(MediaType.TEXT_PLAIN)
    public Response request_authorization(final ServerConfigProperties config, @Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(config.getServerType());
            final String oauth_request_url =  metricServer.setupOauth2Authentication(config.getServerName());
            return oauth_request_url!=null?Response.ok(oauth_request_url).build(): Response.serverError().build();

    }
    @GET
    @Path("token")
    public Response request_accessToken(ServerConfigProperties config,
                                       @Context HttpServletRequest req) {
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(config.getServerType());
        String access_token_response = metricServer.getAccessToken(req, config.getServerName());
        String response = metricServer.saveServerMetadata(config.getServerType(),access_token_response);
        log.info("Server response {}", response);
        return Response.temporaryRedirect(URI.create("http://localhost:6990/bamboo/plugins/servlet/metrics")).build();
    }
    @POST
    @Path("")
    public Response saveServer(ServerConfigProperties config,
                               @Context HttpServletRequest req){
        userService.isAuthenticatedUserAndAdmin();
        MetricServer metricServer = metricServerFactory.getMetricServer(config.getServerType());
        String savedStatus = metricServer.saveServer(config);
        return Response.ok(savedStatus).build();
    }
}
