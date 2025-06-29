package com.ddlabs.atlassian.impl.cache;

import com.ddlabs.atlassian.api.HttpClient;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.config.model.APIAuthenticationType;
import com.ddlabs.atlassian.util.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.*;

import static com.ddlabs.atlassian.impl.events.EventUtils.destroy;

/**
 * Service class responsible for managing local storage operations.
 * This service creates a directory for storing failed data entries
 * and processes tasks asynchronously for saving data into a retry queue.
 */
@Component
public class LocalStorageService {
    private final Logger log = LoggerFactory.getLogger(LocalStorageService.class);
    private final BlockingQueue<String> writeQueue = new LinkedBlockingQueue<>();
    private static final String failed_dir_path = System.getProperty("bamboo.home").concat("/dd-metrics/failed/");
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MetricServerConfigurationCache metricServerConfigurationCache;

    private final HttpClient httpClient;
    private static final int MAX_RETRIES =2;
    private static final int RETRY_BACKOFF_MS = 1000;
    public LocalStorageService(MetricServerConfigurationCache metricServerConfigurationCache, HttpClient httpClient){
        this.metricServerConfigurationCache = metricServerConfigurationCache;
        this.httpClient = httpClient;
        createFailedDirector();
    }
    @PostConstruct
    public void init(){
        executorService.submit(this::workerLoop);
    }

    /**
     * A worker loop that continuously processes tasks from the write queue
     * and writes them to designated files. The method generates unique file names
     * for each task using a randomly generated UUID. Tasks are written to a
     * `failed` directory under the `bamboo.home` system property.

     * The loop continues running until the current thread is interrupted.
     * On catching an {@link InterruptedException} or {@link IOException},
     * it throws a {@link RuntimeException}.

     * Implementation details:
     * - Tasks are retrieved from the {@code writeQueue}.
     * - For each task, a unique file name (UUID-based) is generated.
     * - The task data is written to a file in the designated failure directory
     *   in JSON format using UTF-8 encoding.

     * Throws {@link RuntimeException} if an unexpected error occurs during
     * task processing.

     * This method is designed to run in a dedicated thread initiated by the
     * {@code startWorker()} method as part of the post-initialization process.
     */
    private void workerLoop() {
        while (!Thread.currentThread().isInterrupted()){
            try{
                String task = writeQueue.take();
                final String dataId = UUID.randomUUID().toString();
                Path failedDataFile = Path.of(failed_dir_path).resolve(dataId+".json");
                Files.writeString(failedDataFile, task, StandardCharsets.UTF_8);
            } catch (InterruptedException | IOException e) {
                LogUtils.logError(log, "Worker loop interrupted", e);
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Creates the required parent directory for storing failed data entries if it does not already exist.

     * This method checks the existence of the parent directory specified by the "bamboo.home" system property
     * concatenated with the path "/dd-metrics/failed". If the directory is not present, it creates it.
     * Additionally, logs an informational message upon successful creation of the directory.

     * Throws a {@link RuntimeException} if an {@link IOException} occurs during the directory creation.
     */
    public void createFailedDirector(){
        try{
            Path dir = Paths.get(failed_dir_path);
            if(!Files.exists(dir)){
                Files.createDirectory(dir);
                LogUtils.logInfo(log,"Created failed directory");
            }
        } catch (IOException e) {
          throw  new RuntimeException(e);
        }
    }
    /**
     * Adds a given data point to the retry queue for future processing.

     * The method attempts to add the provided series point to an internal {@code BlockingQueue}.
     * If the queue has available capacity, the series point is added successfully.
     *
     * @param seriesPoint the data point to be added to the retry queue
     */
    public void saveForRetries(String seriesPoint) {
            if(writeQueue.offer(seriesPoint)){
                if(log.isDebugEnabled()){
                    LogUtils.logDebug(log, "Series point {} saved for replay",seriesPoint);
                }
            }else {
                LogUtils.logWarning(log, "Failed to save series point for replay");
        }

    }
    @PreDestroy
    public void cleanUp(){
        destroy(executorService, log);
    }
    /**
     * Attempts to process and resend failed payloads from local storage, maintaining a retry mechanism
     * with exponential backoff in case of transient errors.
     *
     * The method retrieves a list of failed payload files and sequentially processes them. It reads
     * the contents of each file, posts the payload using an HTTP client, and deletes the file upon
     * successful processing. If an error occurs during processing, the method handles specific exceptions
     * and retries the operation up to a maximum number of attempts.
     *
     * If processing fails due to access denial, missing files, or other I/O exceptions, the method will
     * log appropriate messages and increment the retry count. An exponential backoff strategy is applied
     * between retries to ensure resources are gracefully managed and to prevent aggressive re-processing.
     *
     * The retry mechanism terminates under one of these conditions:
     * - A payload is successfully processed.
     * - The maximum number of retries is reached.
     * - The payload list has run out.
     *
     * If all payloads fail to process, an informational message is logged indicating no payloads were
     * successfully sent.
     */
    public void retry() {
        int retries = 0;
        boolean success = false;
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(failed_dir_path),"*.json")) {
        LogUtils.logInfo(log, "Replay queue is empty. Starting to process failed payloads");
        while (!success && retries < MAX_RETRIES) {
            for(Path filePath: stream ){
                try {
                    String payload = Files.readString(filePath);
                    try{
                        postPayload(payload);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                    Files.delete(filePath); // delete on success
                    success = true;
                    LogUtils.logInfo(log, "Series point added to queue and file deleted: {}", filePath);
                } catch (AccessDeniedException ade) {
                    // File is locked or in use by another process
                    LogUtils.logWarning(log, "Access denied when processing file {}. Will retry later.", filePath);
                    retries++;
                    if (backoffSleep()) break;
                } catch (NoSuchFileException nsfe) {
                    // File might have been deleted by another process meanwhile
                    LogUtils.logWarning(log, "File {} not found during processing. Skipping.", filePath);
                    // No retry increment because file no longer exists
                } catch (IOException ioe) {
                    // Other IO exceptions (e.g., file locked, transient issues)
                    LogUtils.logWarning(log, "IO error processing file {}: {}. Retrying...", filePath, ioe.getMessage());
                    retries++;
                    if (backoffSleep()) break;
                } catch (Exception e) {
                    // Other exceptions (e.g., HTTP errors)
                    LogUtils.logError(log, String.format("Unexpected error processing file %s : %s", filePath ,e.getMessage()),
                            e);
                    retries++;
                    if (backoffSleep()) break;
                }
                LogUtils.logInfo(log, "Retry count: {}", retries);
            }
            retries++;
        }
            if (!success) {
                LogUtils.logInfo(log, "No files were successfully sent. Retrying later.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void postPayload(String payload) throws Exception {
        httpClient.post(
                "https://api.datadoghq.com/api/v2/series",
                payload,
                "application/json",
                getAccessKey().getApiKey(),
                getAccessKey().getAppKey()
        );
    }
    private boolean backoffSleep() {
        try {
            Thread.sleep(RETRY_BACKOFF_MS);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    private APIAuthenticationType getAccessKey(){
        return getValidationKeys(metricServerConfigurationCache.getServerConfig("DatadogMetricServer").orElseThrow());
    }
    private APIAuthenticationType getValidationKeys(ServerConfigBuilder serverConfigBuilder) {
        return new APIAuthenticationType(serverConfigBuilder.getClientId(), serverConfigBuilder.getClientSecret(), "");
    }
}
