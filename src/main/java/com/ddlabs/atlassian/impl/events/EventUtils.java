package com.ddlabs.atlassian.impl.events;

import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.cache.MetricServerConfigurationCache;
import com.ddlabs.atlassian.impl.config.model.APIAuthenticationType;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for handling cleanup operations.
 *
 * The EventUtils class provides a method to gracefully shut down
 * an ExecutorService, ensuring proper resource cleanup and
 * logging the shutdown status.
 */
@Component("pEventUtils")
public class EventUtils {
    private final MetricServerConfigurationCache metricServerConfigurationCache;

    public EventUtils(MetricServerConfigurationCache metricServerConfigurationCache) {
        this.metricServerConfigurationCache = metricServerConfigurationCache;
    }

    /**
     * Shuts down the provided ExecutorService gracefully, attempting to terminate all tasks.
     * Logs the shutdown completion status using the provided Logger.
     *
     * @param executorService The ExecutorService to shut down. It must not be null.
     * @param log The Logger to use for logging the shutdown status. It must not be null.
     */
    public static void destroy(ExecutorService executorService, Logger log) {
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(60, TimeUnit.SECONDS)){
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdown();
            Thread.currentThread().interrupt();
        }finally {
            LogUtils.logInfo(log, "Shutdown completed");
        }
    }

    public APIAuthenticationType getAccessKey(){
        return getValidationKeys(metricServerConfigurationCache.getServerConfig("DatadogMetricServer").orElseThrow());
    }
    private APIAuthenticationType getValidationKeys(ServerConfigBuilder serverConfigBuilder) {
        return new APIAuthenticationType(serverConfigBuilder.getClientId(), serverConfigBuilder.getClientSecret(), "");
    }

}
