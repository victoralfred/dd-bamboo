package com.ddlabs.atlassian.impl.events;

import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsPluginEventListeners{
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final BuildEventHandlers buildEventHandlers;
    public MetricsPluginEventListeners(BuildEventHandlers buildEventHandlers) {
        this.buildEventHandlers = buildEventHandlers;
    }
    @EventListener
    public void onBuildFinished(BuildFinishedEvent event) {
        buildEventHandlers.handleBuildEvent(event);
    }
}
