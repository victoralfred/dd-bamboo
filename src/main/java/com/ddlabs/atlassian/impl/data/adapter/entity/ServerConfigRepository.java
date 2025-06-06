package com.ddlabs.atlassian.impl.data.adapter.entity;

import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.exception.DataAccessException;

import java.util.List;

/**
 * Repository interface for server configurations.
 */
public interface ServerConfigRepository {
    
    /**
     * Finds a server configuration by server type.
     *
     * @param serverType The server type
     * @return The server configuration, or null if not found
     * @throws DataAccessException If an error occurs
     */
    ServerConfigBuilder findByServerType(String serverType) throws DataAccessException;
    
    /**
     * Finds all server configurations.
     *
     * @return The list of server configurations
     * @throws DataAccessException If an error occurs
     */
    List<ServerConfigBuilder> findAll() throws DataAccessException;
    
    /**
     * Saves a server configuration.
     *
     * @param config The server configuration to save
     * @return The saved server message
     * @throws DataAccessException If an error occurs
     */
    String save(ServerConfigBuilder config) throws DataAccessException;
    
    /**
     * Updates a server configuration.
     *
     * @param config The server configuration to update
     * @throws DataAccessException If an error occurs
     */
    void update(ServerConfigBuilder config) throws DataAccessException;

    /**
     * Deletes a server configuration by server type.
     *
     * @param serverType The server type
     * @throws DataAccessException If an error occurs
     */
    void delete(String serverType) throws DataAccessException;
}
