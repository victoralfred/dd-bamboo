package com.ddlabs.atlassian.rest;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.remote.MetricServer;
import com.ddlabs.atlassian.remote.MetricServerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class MetricsApiClient {
    private final MetricServerFactory metricServerFactory;
    private final OAuth2AuthorizationService oauth2AuthorizationService;
    @Inject
    public MetricsApiClient(MetricServerFactory metricServerFactory, OAuth2AuthorizationService oauth2AuthorizationService) {
        this.metricServerFactory = metricServerFactory;
        this.oauth2AuthorizationService = oauth2AuthorizationService;
    }

    @GET
    @Path("kpi/{serverType}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response request_authorization(@PathParam("serverType") String serverType, @Context HttpServletRequest req) {
        MetricServer metricServer = metricServerFactory.getMetricServer(serverType);
        try {
            oauth2AuthorizationService.refreshToken(metricServer);
            return Response.ok("ok").build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
