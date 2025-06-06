package com.ddlabs.atlassian.impl.events;

import com.ddlabs.atlassian.api.HttpClient;
import com.ddlabs.atlassian.impl.http.TokenStore;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

@Service
public class ConcurrentMetricHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final HttpClient httpClient;
    private final TokenStore tokenStore;
    private final BlockingQueue<String> seriesQueue = new LinkedBlockingQueue<>();
    private ExecutorService executorService;
    private static final int MAX_RETRIES =5;
    private static final int RETRY_BACKOFF_MS = 1000;
    private static final int MAX_NUMER_OF_WORKERS = 3;
    public ConcurrentMetricHandler(HttpClient httpClient, TokenStore tokenStore) {
        this.httpClient = httpClient;
        this.tokenStore = tokenStore;
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
    private void processWithRetries(String seriesPoint) {
        int retries = 0;
        boolean success = false;
        while(!success && retries < MAX_RETRIES){
            try {
                httpClient.post("https://api.datadoghq.com/api/v2/series", seriesPoint, "application/json",
                        "API_KEY" );
                success = true;
                    LogUtils.logInfo(log, "Series point added to queue");
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
            persistSeriesForReplay(seriesPoint);
        }
    }
    private void persistSeriesForReplay(String seriesPoint) {
        LogUtils.logInfo(log, "Persisting series point: " + seriesPoint);
        LogUtils.logInfo(log, "Size of series: " + seriesQueue.size());
    }
    @PreDestroy
    private void shutDown(){
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
}
