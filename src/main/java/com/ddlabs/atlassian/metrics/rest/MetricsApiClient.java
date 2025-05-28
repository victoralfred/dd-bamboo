package com.ddlabs.atlassian.metrics.rest;

import com.ddlabs.atlassian.api.OAuth2AuthorizationService;
import com.ddlabs.atlassian.metrics.remote.MetricServer;
import com.ddlabs.atlassian.metrics.remote.MetricServerFactory;
import com.ddlabs.atlassian.metrics.remote.datadog.MetricsApiProvider;

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
    private final MetricsApiProvider metricsApiProvider;
    @Inject
    public MetricsApiClient(MetricServerFactory metricServerFactory, MetricsApiProvider metricsApiProvider) {
        this.metricServerFactory = metricServerFactory;
        this.metricsApiProvider = metricsApiProvider;
    }

    @GET
    @Path("kpi/{serverType}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response request_authorization(@PathParam("serverType") String serverType, @Context HttpServletRequest req) {
        MetricServer metricServer = metricServerFactory.getMetricServer(serverType);
        try {
            return Response.ok(metricsApiProvider.getV1Api(serverType)).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
