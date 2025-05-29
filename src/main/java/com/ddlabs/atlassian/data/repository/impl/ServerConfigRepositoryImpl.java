package com.ddlabs.atlassian.data.repository.impl;

import com.ddlabs.atlassian.api.PluginDaoRepository;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.data.repository.ServerConfigRepository;
import com.ddlabs.atlassian.exception.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ServerConfigRepositoryImpl implements ServerConfigRepository {
    private final PluginDaoRepository pluginDaoRepository;

    public ServerConfigRepositoryImpl(PluginDaoRepository pluginDaoRepository) {
        this.pluginDaoRepository = pluginDaoRepository;
    }

    @Override
    public MSConfigEntity findByServerType(String serverType) throws DataAccessException {
        return  pluginDaoRepository.getServerConfigByType(serverType);
    }

    @Override
    public List<ServerConfigDTO> findAll() throws DataAccessException {
        return List.of();
    }

    @Override
    public String save(ServerConfigDTO config) throws DataAccessException {
        return pluginDaoRepository.saveServerConfig(config);
    }

    @Override
    public void update(MSConfigEntity config) throws DataAccessException {
        pluginDaoRepository.updateServerConfig(config);
    }

    @Override
    public void delete(String serverType) throws DataAccessException {

    }
}
