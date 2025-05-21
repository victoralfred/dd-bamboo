package com.ddlabs.atlassian.api;

import com.atlassian.activeobjects.tx.Transactional;
import com.ddlabs.atlassian.model.ServerConfigurationFields;
/**
 * Interface for managing plugin data access operations.
 * This interface defines methods for saving server configuration fields.
 */
@Transactional
public interface PluginDaoRepository {
    /**
     * Retrieves the server configuration fields.
     *
     * @return the server configuration fields
     */
    String saveServerConfig(ServerConfigurationFields serverConfig);
}