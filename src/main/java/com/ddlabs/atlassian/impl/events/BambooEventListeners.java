package com.ddlabs.atlassian.impl.events;

import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.atlassian.event.api.EventListener;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class BambooEventListeners {
    private final BambooEventHandlers bambooEventHandlers;
    public BambooEventListeners(BambooEventHandlers bambooEventHandlers) {
        this.bambooEventHandlers = bambooEventHandlers;
    }
    @EventListener
    public void onBuildFinished(BuildFinishedEvent event) {
        bambooEventHandlers.handleBuildEvent(event);
    }
}
