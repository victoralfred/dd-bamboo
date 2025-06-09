package com.ddlabs.atlassian.impl.events;

import com.ddlabs.atlassian.api.HttpClient;
import com.ddlabs.atlassian.impl.cache.LocalStorageService;
import com.ddlabs.atlassian.impl.cache.MetricServerConfigurationCache;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.config.model.APIAuthenticationType;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * The EventMessagePusher class is responsible for pushing event metrics data to an external
 * API endpoint, specifically designed to interact with Datadog Metric Server. This service
 * operates in a multi-threaded environment to handle and process queued metrics efficiently.
 *
 * <h2>Core Responsibilities:</h2>
 * - Maintains a queue of series data points to be processed.
 * - Sends the series data points to an external metric server API with configurable retries.
 * - Persists failed data for future retries using local storage.
 * - Provides multithreaded processing via workers to ensure scalability and efficiency.
 *
 * <h2>Lifecycle Management:</h2>
 * - Initializes worker threads using `@PostConstruct` to begin processing the queue.
 * - Cleans up resources and worker threads during shutdown using `@PreDestroy`.
 *
 * <h2>Retry Logic:</h2>
 * - Implements a retry mechanism for sending data to the API endpoint.
 * - Applies a fixed retry backoff to reduce contention and stagger retries.
 * - Fails gracefully by persisting unprocessed data for additional retries later.
 *
 * <h2>Dependencies:</h2>
 * - HttpClient: Used for sending HTTP POST requests to the API endpoint.
 * - MetricServerConfigurationCache: Fetches authentication details and configuration for the external API.
 * - LocalStorageService: Handles persistence of failed series points for future replay attempts.
 *
 * This class provides a robust and resilient mechanism for handling and pushing event metrics
 * while supporting retry capabilities and scalable processing.
 */
@Service
public class EventMessagePusher {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final HttpClient httpClient;
    private final MetricServerConfigurationCache metricServerConfigurationCache;
    private final BlockingQueue<String> seriesQueue = new LinkedBlockingQueue<>();
    private final LocalStorageService localStorageService;
    private ExecutorService executorService;
    private static final int MAX_RETRIES =5;
    private static final int RETRY_BACKOFF_MS = 1000;
    private static final int MAX_NUMER_OF_WORKERS = 3;
    public EventMessagePusher(HttpClient httpClient, MetricServerConfigurationCache metricServerConfigurationCache, LocalStorageService localStorageService) {
        this.httpClient = httpClient;
        this.metricServerConfigurationCache = metricServerConfigurationCache;
        this.localStorageService = localStorageService;
    }
    public boolean addSeriesToQueue(String series) {
        return seriesQueue.offer(series);
    }
    @PostConstruct
    public void startWorker() {
        LogUtils.logInfo(log, "Metric Handler started");
        executorService = Executors.newFixedThreadPool(MAX_NUMER_OF_WORKERS);
        for (int i = 0; i < MAX_NUMER_OF_WORKERS; i++) {
            executorService.submit(this::workerLoop);
        }
    }
    private void workerLoop() {
        while (true){
            try{
                String series = seriesQueue.take();
                processWithRetries(series);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Processes a given series point by sending it to an external API endpoint with retries
     * in case of failures. If the maximum retries are reached without success, the series
     * point is saved for future retry.
     *
     * @param seriesPoint the data point to send to the external API
     */
    private void processWithRetries(String seriesPoint) {
        int retries = 0;
        boolean success = false;
        while(!success && retries < MAX_RETRIES){
            try {
                httpClient.post("https://api.datadoghq.com/api/v2/series", seriesPoint, "application/json",
                        getAccessKey().getApiKey(), getAccessKey().getAppKey() );
                success = true;
                    LogUtils.logInfo(log, "Series point added to queue");
                Thread.sleep(500);
            }catch (Exception e) {
                retries++;
                try{
                    Thread.sleep(RETRY_BACKOFF_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        if(!success){
            localStorageService.saveForRetries(seriesPoint);
            if(log.isDebugEnabled()){
                LogUtils.logDebug(log, "Series point {} not sent. Adding to Replay queue",seriesPoint);
            }
        }
    }

    @PreDestroy
    private void shutDown(){
        EventUtils.destroy(executorService, log);
    }
    private APIAuthenticationType getAccessKey(){
        return getValidationKeys(metricServerConfigurationCache.getServerConfig("DatadogMetricServer").orElseThrow());
    }
    private APIAuthenticationType getValidationKeys(ServerConfigBuilder serverConfigBuilder) {
        return new APIAuthenticationType(serverConfigBuilder.getClientId(), serverConfigBuilder.getClientSecret(), "");
    }
}
