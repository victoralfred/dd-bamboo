package com.ddlabs.atlassian.dao;

import com.ddlabs.atlassian.dao.adapter.PluginDaoRepositoryAdapter;
import com.ddlabs.atlassian.dao.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.dao.adapter.entity.ConfigRepository;
import com.ddlabs.atlassian.impl.exception.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ConfigRepositoryImpl implements ConfigRepository {
    private final PluginDaoRepositoryAdapter pluginDaoRepository;
    public ConfigRepositoryImpl(PluginDaoRepositoryAdapter pluginDaoRepository) {
        this.pluginDaoRepository = pluginDaoRepository;
    }

    @Override
    public ServerConfigBuilder findByServerType(String serverType) throws DataAccessException {
        return  pluginDaoRepository.getServerConfigByType(serverType);
    }

    @Override
    public List<ServerConfigBuilder> findAll() throws DataAccessException {
        return pluginDaoRepository.getAllServerConfigs();
    }

    @Override
    public String save(ServerConfigBuilder config) throws DataAccessException {
        return pluginDaoRepository.saveServerConfig(config);
    }

    @Override
    public void update(ServerConfigBuilder config) throws DataAccessException {
        pluginDaoRepository.updateServerConfig(config);
    }

    @Override
    public void delete(String serverType) throws DataAccessException {
        pluginDaoRepository.deleteServerConfig(serverType);
    }
}
