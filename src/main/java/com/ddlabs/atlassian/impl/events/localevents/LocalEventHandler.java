package com.ddlabs.atlassian.impl.events.localevents;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.ddlabs.atlassian.impl.cache.MetricServerConfigurationCache;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class LocalEventHandler implements InitializingBean, DisposableBean {
    private final MetricServerConfigurationCache metricServerConfigurationCache;
    @ComponentImport
    private final EventPublisher eventPublisher;
    public LocalEventHandler(MetricServerConfigurationCache metricServerConfigurationCache, EventPublisher eventPublisher) {
        this.metricServerConfigurationCache = metricServerConfigurationCache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }
    @EventListener
    public void onServerDeletedEvent(ServerDeletedEvent serverDeletedEvent) {
        metricServerConfigurationCache.clearCache();
    }
}

