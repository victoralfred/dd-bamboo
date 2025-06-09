package com.ddlabs.atlassian.impl.cache;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.dao.adapter.entity.ConfigRepository;
import com.ddlabs.atlassian.util.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code MetricServerConfigurationCache} class provides a caching mechanism for server configurations.
 * It serves as an intermediary layer between the underlying configuration repositories and the application,
 * maintaining a cache of server configurations for quick access, minimizing database queries and improving
 * performance. The cache is implemented as a thread-safe {@code ConcurrentHashMap}.
 *
 * This service enables:
 * - Caching and retrieval of server configurations by server type.
 * - Updating and removing specific server configurations in the cache.
 * - Clearing and populating the cache with configurations from the database.
 *
 * The class operates primarily on {@code ServerConfigBuilder} objects, which represent individual server
 * configurations, and interacts with the provided {@code ConfigRepository} instances for database operations.
 *
 * Key features:
 * - Retrieval of server configuration using the server type.
 * - Automatic reloading of cache entries when data is missing or outdated.
 * - Comprehensive logging of cache operations for debugging and monitoring purposes.
 */
@Service
public class MetricServerConfigurationCache {
    private final ConfigRepository configRepository;
    private final ConfigRepository repository;
    private static final Logger log = LoggerFactory.getLogger(MetricServerConfigurationCache.class);
    private final Map<String, ServerConfigBuilder> CACHE = new ConcurrentHashMap<>();

    public MetricServerConfigurationCache(ConfigRepository configRepository, ConfigRepository repository) {
        this.configRepository = configRepository;
        this.repository = repository;
    }

    /**
     * Retrieves the server configuration for the specified server type from the cache.
     * If the configuration is not found in the cache, it fetches the data from the database,
     * updates the cache, and retrieves the configuration again.
     *
     * @param serverType the type of the server whose configuration is being retrieved. Must not be null.
     * @return an {@link Optional} containing the {@code ServerConfigBuilder} if the configuration is found;
     *         otherwise, an empty {@code Optional}.
     */
    public Optional<ServerConfigBuilder> getServerConfig(@NotNull String serverType) {
        ServerConfigBuilder config = CACHE.get(serverType);
        if(config==null){
            fetchFromDatabase();
             config = CACHE.get(serverType);
        }
        return Optional.ofNullable(config);
    }

    /**
     * Updates the server configuration cache with the provided configuration.
     *
     * This method adds or updates the configuration for a specific server type in the cache.
     * It also logs an informational message indicating that the cache has been updated
     * for the given server type.
     *
     * @param serverType the type of the server whose configuration is being updated. Must not be null.
     * @param config the configuration to be stored in the cache. Must not be null.
     */
    public void putServerConfig(@NotNull String serverType, @NotNull ServerConfigBuilder config) {
        CACHE.put(serverType, config);
        LogUtils.logInfo(log,"Cache updated for server type {}", serverType);
    }

    /**
     * Updates the server configuration cache for a specific server type.
     *
     * This method fetches the server configuration associated with the provided
     * server type from the `configRepository`, updates the cache by either replacing
     * the existing entry or adding a new one, and logs the update process.
     *
     * Note:
     * - If an existing cache entry for the specified server type is found, it is removed and replaced.
     * - An informational log is generated indicating the update status.
     *
     * @param serverType the type of server whose configuration needs to be updated. Must not be null.
     */
    public void updateServerConfig(@NotNull String serverType) {
        // Implement reload/refresh logic here if applicable
        log.info("Updating cache for server type {}", serverType);
        ServerConfigBuilder data = configRepository.findByServerType(serverType);
        if(CACHE.remove(serverType)!=null){
            CACHE.put(serverType, data);
            LogUtils.logInfo(log,"Cache updated for server type {}", serverType);
            return;
        }
        CACHE.put(serverType, data);
        LogUtils.logInfo(log,"Cache updated for server type {}", serverType);
    }

    /**
     * Retrieves all server configuration objects currently stored in the cache.
     *
     * If the cache is empty, the method triggers a database fetch to populate
     * the cache and retries the retrieval.
     *
     * @return a list of {@code ServerConfigBuilder} objects representing the server
     * configurations available in the cache. The list will not contain null values
     * but may be empty if the database contains no server configurations.
     */
    public List<ServerConfigBuilder> getAllServerConfigs() {
        var result = CACHE.values().stream().filter(Objects::nonNull).toList();
        if(result.isEmpty()){
            fetchFromDatabase();
            result = CACHE.values().stream().filter(Objects::nonNull).toList();
        }
        return result;
    }

    /**
     * Removes a specific server configuration entry from the cache.
     *
     * This method deletes the cached configuration associated with the
     * provided server type and logs an informational message indicating
     * that the entry has been removed.
     *
     * @param serverType the type of the server whose configuration entry
     *                   is to be removed from the cache. Must not be null.
     */
    public void removeServerConfig(@NotNull String serverType) {
        CACHE.remove(serverType);
        log.info("Cache entry removed for server type {}", serverType);
    }

    /**
     * Clears all entries in the server configuration cache.
     *
     * This method removes all server configuration data currently stored in the
     * internal cache and logs an informational message indicating the cache has
     * been cleared. It is typically used to reset the cache to an empty state.
     *
     * Note:
     * - This operation is irreversible and will clear all cached data.
     * - Use this method when the entire cache needs to be invalidated.
     */
    public void clearCache() {
        CACHE.clear();
        log.info("Cache cleared");
    }

    /**
     * Updates the cache with server configurations retrieved from the database.
     *
     * This method retrieves all server configurations from two different repositories
     * and populates the cache with their data. For each configuration, it logs an
     * informational message indicating that the cache has been updated for a specific
     * server type. At the end of the process, a summary log message is added indicating
     * that the cache has been fully updated with all server configurations.
     *
     * The method performs the following steps:
     * - Fetches all configurations from the `configRepository`.
     * - Updates the cache with the configurations from the `configRepository` while logging the updates.
     * - Fetches all configurations from the `repository`.
     * - Updates the cache with the configurations from the `repository` while logging the updates.
     * - Logs a final informational message indicating the completion of the cache update.
     *
     * Note:
     * - Assumes non-null server configurations are returned by the repositories.
     * - Relies on the `LogUtils.logInfo` utility for logging details.
     */
    public void fetchFromDatabase(){
        configRepository.findAll().forEach(config -> {
             CACHE.put(config.getServerType(),config);
             LogUtils.logInfo(log,"Cache updated for server type {}", config.getServerType());
         });
         repository.findAll().forEach(config -> {
             CACHE.put(config.getServerType(),config);
             LogUtils.logInfo(log,"Cache updated for server type {}", config.getServerType());
         });
         LogUtils.logInfo(log,"Cache updated with all server configs");
    }
}
