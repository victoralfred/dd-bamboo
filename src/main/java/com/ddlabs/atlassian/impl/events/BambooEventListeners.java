package com.ddlabs.atlassian.impl.events;

import com.atlassian.bamboo.deployments.execution.events.DeploymentEvent;
import com.atlassian.bamboo.deployments.execution.events.DeploymentFinishedEvent;
import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.atlassian.bamboo.event.ChainCompletedEvent;
import com.atlassian.bamboo.event.ServerStartedEvent;
import com.atlassian.event.api.EventListener;
import com.ddlabs.atlassian.impl.cache.MetricServerConfigurationCache;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class BambooEventListeners {
    private static final Logger log = LoggerFactory.getLogger(BambooEventListeners.class);
    private final BambooEventHandlers bambooEventHandlers;
    private final MetricServerConfigurationCache cache;
    public BambooEventListeners(BambooEventHandlers bambooEventHandlers, MetricServerConfigurationCache cache) {
        this.bambooEventHandlers = bambooEventHandlers;
        this.cache = cache;
    }

    /**
     * Event listener triggered when the server starts.
     * This method is executed when a {@link ServerStartedEvent} is published.
     * It ensures that the cache is populated with all server configurations.
     *
     * @param startedEvent the event object representing the server start event.
     */
    @EventListener
    public void onServerStarted(ServerStartedEvent startedEvent){
        cache.getAllServerConfigs();
        LogUtils.logInfo(log,"Server started, loading configuration cache if exists");
    }
    @EventListener
    public void onBuildFinished(BuildFinishedEvent event) {
        bambooEventHandlers.handleBuildEvent(event);
    }
    @EventListener
    public void onChainCompletedEvent(ChainCompletedEvent event){
        LogUtils.logInfo(log,"Chain Completed Event Received");
        LogUtils.logInfo(log, "Cahin key {}", event.getPlanKey().getKey());
    }
    @EventListener
    public void onDeploymentCompleted(DeploymentFinishedEvent deploymentEvent){
        LogUtils.logInfo(log,"Deployment {} Completed Event Received",deploymentEvent.getResultKey());
        LogUtils.logInfo(log, "{}", deploymentEvent.getTimestamp());
        LogUtils.logInfo(log, "Deployment final {}",deploymentEvent.toString());


    }

}
