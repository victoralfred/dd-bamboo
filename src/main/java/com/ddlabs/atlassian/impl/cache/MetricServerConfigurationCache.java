package com.ddlabs.atlassian.impl.cache;

import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class MetricServerConfigurationCache {
    private final Map<String, ServerConfigBuilder> CHACHE
            = new ConcurrentHashMap<>();
    /**Loads the cache with initial data if data is available when the plugin loads,
     * otherwise it will be loaded on demand.
     */
    @PostConstruct
    public void init() {
        // Initialize the cache if needed
    }
}
