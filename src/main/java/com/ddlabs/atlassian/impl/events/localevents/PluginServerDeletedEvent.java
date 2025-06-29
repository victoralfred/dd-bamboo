package com.ddlabs.atlassian.impl.events.localevents;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

@Component
public class PluginServerDeletedEvent {
    @ComponentImport
    private final EventPublisher eventPublisher;
    public PluginServerDeletedEvent(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    public void publish(ServerDeletedEvent serverDeletedEvent) {
        eventPublisher.publish(serverDeletedEvent);
    }
}
