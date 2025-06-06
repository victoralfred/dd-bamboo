package com.ddlabs.atlassian.impl.data;

import com.ddlabs.atlassian.impl.data.adapter.PluginDaoRepositoryAdapter;
import com.ddlabs.atlassian.impl.data.adapter.dto.ServerConfigBuilder;
import com.ddlabs.atlassian.impl.data.adapter.entity.ServerConfigRepository;
import com.ddlabs.atlassian.impl.exception.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ServerConfigRepositoryImpl implements ServerConfigRepository {
    private final PluginDaoRepositoryAdapter pluginDaoRepository;
    public ServerConfigRepositoryImpl(PluginDaoRepositoryAdapter pluginDaoRepository) {
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

    }
}
