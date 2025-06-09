package com.ddlabs.atlassian.impl.events;

import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.atlassian.bamboo.event.ChainCompletedEvent;
import com.atlassian.event.api.EventListener;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class BambooEventListeners {
    private static final Logger log = LoggerFactory.getLogger(BambooEventListeners.class);
    private final BambooEventHandlers bambooEventHandlers;
    public BambooEventListeners(BambooEventHandlers bambooEventHandlers) {
        this.bambooEventHandlers = bambooEventHandlers;
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
}
