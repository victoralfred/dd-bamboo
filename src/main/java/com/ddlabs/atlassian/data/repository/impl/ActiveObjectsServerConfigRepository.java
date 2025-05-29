package com.ddlabs.atlassian.data.repository.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.data.mapper.ServerConfigMapper;
import com.ddlabs.atlassian.data.repository.ServerConfigRepository;
import com.ddlabs.atlassian.exception.DataAccessException;
import com.ddlabs.atlassian.exception.ErrorCode;
import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ActiveObjects implementation of the ServerConfigRepository interface.
 */
@Repository
public class ActiveObjectsServerConfigRepository implements ServerConfigRepository {
    private static final Logger log = LoggerFactory.getLogger(ActiveObjectsServerConfigRepository.class);
    
    private final ActiveObjects activeObjects;
    private final ServerConfigMapper serverConfigMapper;
    
    @Inject
    public ActiveObjectsServerConfigRepository(@ComponentImport ActiveObjects activeObjects, 
                                              ServerConfigMapper serverConfigMapper) {
        this.activeObjects = ValidationUtils.validateNotNull(activeObjects, "ActiveObjects cannot be null");
        this.serverConfigMapper = ValidationUtils.validateNotNull(serverConfigMapper, "ServerConfigMapper cannot be null");
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServerConfigDTO findByServerType(String serverType) throws DataAccessException {
        try {
            ValidationUtils.validateNotEmpty(serverType, "Server type cannot be empty");
            
            MSConfigEntity[] entities = activeObjects.find(MSConfigEntity.class, 
                    Query.select().where("SERVER_TYPE = ?", serverType));
            
            if (entities.length == 0) {
                return null;
            }
            
            return serverConfigMapper.toDto(entities[0]);
        } catch (Exception e) {
            LogUtils.logError(log, "Error finding server config by type: " + serverType, e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR, 
                    "Error finding server config by type: " + serverType, 
                    "Failed to retrieve server configuration", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerConfigDTO> findAll() throws DataAccessException {
        try {
            MSConfigEntity[] entities = activeObjects.find(MSConfigEntity.class);
            List<ServerConfigDTO> configs = new ArrayList<>(entities.length);
            
            for (MSConfigEntity entity : entities) {
                configs.add(serverConfigMapper.toDto(entity));
            }
            
            return configs;
        } catch (Exception e) {
            LogUtils.logError(log, "Error finding all server configs", e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR, 
                    "Error finding all server configs", 
                    "Failed to retrieve server configurations", e);
        }
    }
    
    @Override
    @Transactional
    public ServerConfigDTO save(ServerConfigDTO config) throws DataAccessException {
        try {
            ValidationUtils.validateNotNull(config, "Server config cannot be null");
            ValidationUtils.validateNotEmpty(config.getServerType(), "Server type cannot be empty");
            
            MSConfigEntity entity = activeObjects.create(MSConfigEntity.class);
            serverConfigMapper.updateEntityFromDto(entity, config);
            entity.save();
            
            return serverConfigMapper.toDto(entity);
        } catch (Exception e) {
            LogUtils.logError(log, "Error saving server config", e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR, 
                    "Error saving server config: " + e.getMessage(), 
                    "Failed to save server configuration", e);
        }
    }
    
    @Override
    @Transactional
    public void update(ServerConfigDTO config) throws DataAccessException {
        try {
            ValidationUtils.validateNotNull(config, "Server config cannot be null");
            ValidationUtils.validateNotEmpty(config.getServerType(), "Server type cannot be empty");
            
            MSConfigEntity[] entities = activeObjects.find(MSConfigEntity.class, 
                    Query.select().where("SERVER_TYPE = ?", config.getServerType()));
            
            if (entities.length == 0) {
                throw new DataAccessException(ErrorCode.ENTITY_NOT_FOUND, 
                        "No server config found for type: " + config.getServerType(), 
                        "Server configuration not found");
            }
            
            MSConfigEntity entity = entities[0];
            serverConfigMapper.updateEntityFromDto(entity, config);
            entity.save();
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.logError(log, "Error updating server config", e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR, 
                    "Error updating server config: " + e.getMessage(), 
                    "Failed to update server configuration", e);
        }
    }
    
    @Override
    @Transactional
    public void delete(String serverType) throws DataAccessException {
        try {
            ValidationUtils.validateNotEmpty(serverType, "Server type cannot be empty");
            
            MSConfigEntity[] entities = activeObjects.find(MSConfigEntity.class, 
                    Query.select().where("SERVER_TYPE = ?", serverType));
            
            if (entities.length > 0) {
                activeObjects.delete(entities);
                LogUtils.logInfo(log, "Deleted server config for type: {}", serverType);
            } else {
                LogUtils.logWarning(log, "No server config found for type: {}", serverType);
            }
        } catch (Exception e) {
            LogUtils.logError(log, "Error deleting server config", e);
            throw new DataAccessException(ErrorCode.DATA_ACCESS_ERROR, 
                    "Error deleting server config: " + e.getMessage(), 
                    "Failed to delete server configuration", e);
        }
    }
}
