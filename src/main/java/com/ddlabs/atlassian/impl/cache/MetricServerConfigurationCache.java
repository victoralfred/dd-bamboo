package com.ddlabs.atlassian.impl.cache;

import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.data.adapter.entity.ServerConfigRepository;
import com.ddlabs.atlassian.util.LogUtils;
import net.java.ao.Entity;
import net.java.ao.Preload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class MetricServerConfigurationCache {
    private static final String DEFAULT_SERVER_TYPE = "com.ddlabs.bamboo-datadog-metrics";
    private static final Logger log = LoggerFactory.getLogger(MetricServerConfigurationCache.class);
    private final Map<String, ServerConfigBuilder> CACHE = new ConcurrentHashMap<>();
    private final ServerConfigRepository serverConfigRepository;
    public MetricServerConfigurationCache(ServerConfigRepository serverConfigRepository) {
        this.serverConfigRepository = serverConfigRepository;
    }

    /**Loads the cache with initial data if data is available when the plugin loads,
     * otherwise it will be loaded on demand.
     */
    @EventListener
    public void init(PluginEnabledEvent event) {
        LogUtils.logInfo(log, "Initializing cache for plugin {}", event.getPlugin().getKey());
      if(event.getPlugin().getKey().equals(DEFAULT_SERVER_TYPE)){
          serverConfigRepository.findAll().stream().filter(ServerConfigBuilder::nonNull)
                  .forEach(config -> CACHE.put(config.getServerType(), config));
          LogUtils.logInfo(log, "Cache loaded with {} entries", CACHE.size());
      }else{
          LogUtils.logInfo(log, "Plugin {} is not enabled, skipping cache initialization", DEFAULT_SERVER_TYPE);
      }
    }
    public Optional<ServerConfigBuilder> getServerConfig(String serverType) {
        return Optional.ofNullable(CACHE.get(serverType));
    }
    public void updateServerConfig(String serverType) {
        int size = Optional.ofNullable(serverConfigRepository.findByServerType(serverType))
                .stream().map(result->CACHE.put(serverType, result))
                .toList().size();
    }
    public List<ServerConfigBuilder> getAllServerConfigs() {
        return serverConfigRepository.findAll();
    }
    @PreDestroy
    public void destroy() {
        CACHE.clear();
    }
}
