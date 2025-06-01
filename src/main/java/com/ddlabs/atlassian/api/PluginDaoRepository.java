package com.ddlabs.atlassian.api;

import com.atlassian.activeobjects.tx.Transactional;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.config.model.ConfiguredMetricServers;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;

import java.util.List;

/**
 * Interface for managing plugin data access operations.
 * This interface defines methods for saving server configuration fields.
 */
@Transactional
public interface PluginDaoRepository {
    /**
     * Saves the server configuration properties to the database.
     *
     * @param properties the server configuration properties to save
     * @return a message indicating the result of the save operation
     */
    String saveServerConfig(ServerConfigDTO properties);
    /**
     * Retrieves the server configuration by its type.
     *
     * @param serverType the type of the server to retrieve
     * @return the server configuration if found, otherwise null
     */
     MSConfigEntity getServerConfigByType(String serverType);
    /**
     * Updates the server configuration in the database.
     *
     * @param serverConfig the server configuration to update
     */
    <T extends MSConfigEntity> void updateServerConfig(T serverConfig);
    /**
     * Retrieves all configured metric servers.
     *
     * @return a list of configured metric servers
     */
    List<ConfiguredMetricServers> getAllServerConfigs();
    /**
     * Deletes a server configuration by its type.
     *
     * @param serverType the type of the server to delete
     */
    void deleteServerConfig(String serverType);
    /**
     * Retrieves all {@link MSConfigEntity} configuration from Database
     * @return the server configuration if found, otherwise null
     */
    List<MSConfigEntity> getMsConfig();
}