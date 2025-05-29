package com.ddlabs.atlassian.data.mapper;

import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;

/**
 * Mapper for converting between MSConfigEntity and ServerConfigDTO.
 */
public class ServerConfigMapper {
    
    /**
     * Converts a MSConfigEntity to a ServerConfigDTO.
     *
     * @param entity The entity to convert
     * @return The DTO
     */
    public ServerConfigDTO toDto(MSConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ServerConfigDTO dto = new ServerConfigDTO();
        dto.setServerType(entity.getServerType());
        dto.setServerName(entity.getServerName());
        dto.setDescription(entity.getDescription());
        dto.setClientId(entity.getClientId());
        dto.setClientSecret(entity.getClientSecret());
        dto.setRedirectUrl(entity.getRedirectUrl());
        dto.setCodeVerifier(entity.getCodeVerifier());
        dto.setCodeChallenge(entity.getCodeChallenge());
        dto.setCodeCreationTime(entity.getCodeCreationTime());
        dto.setCodeExpirationTime(entity.getCodeExpirationTime());
        dto.setAccessToken(entity.getAccessToken());
        dto.setRefreshToken(entity.getRefreshToken());
        dto.setTokenType(entity.getTokenType());
        dto.setScope(entity.getScope());
        dto.setAccessTokenExpiry(entity.getAccessTokenExpiry());
        dto.setConfigured(entity.isConfigured());
        dto.setEnabled(entity.isEnabled());
        dto.setApiEndpoint(entity.getApiEndpoint());
        dto.setOauthEndpoint(entity.getOauthEndpoint());
        dto.setTokenEndpoint(entity.getTokenEndpoint());
        dto.setSite(entity.getSite());
        dto.setDomain(entity.getDomain());
        dto.setOrgId(entity.getOrgId());
        dto.setOrgName(entity.getOrgName());
        
        return dto;
    }
    
    /**
     * Updates an MSConfigEntity from a ServerConfigDTO.
     *
     * @param entity The entity to update
     * @param dto The DTO with the new values
     */
    public void updateEntityFromDto(MSConfigEntity entity, ServerConfigDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setServerType(dto.getServerType());
        entity.setServerName(dto.getServerName());
        entity.setDescription(dto.getDescription());
        entity.setClientId(dto.getClientId());
        entity.setClientSecret(dto.getClientSecret());
        entity.setRedirectUrl(dto.getRedirectUrl());
        entity.setCodeVerifier(dto.getCodeVerifier());
        entity.setCodeChallenge(dto.getCodeChallenge());
        entity.setCodeCreationTime(dto.getCodeCreationTime());
        entity.setCodeExpirationTime(dto.getCodeExpirationTime());
        entity.setAccessToken(dto.getAccessToken());
        entity.setRefreshToken(dto.getRefreshToken());
        entity.setTokenType(dto.getTokenType());
        entity.setScope(dto.getScope());
        entity.setAccessTokenExpiry(dto.getAccessTokenExpiry());
        entity.setConfigured(dto.isConfigured());
        entity.setEnabled(dto.isEnabled());
        entity.setApiEndpoint(dto.getApiEndpoint());
        entity.setOauthEndpoint(dto.getOauthEndpoint());
        entity.setTokenEndpoint(dto.getTokenEndpoint());
        entity.setSite(dto.getSite());
        entity.setDomain(dto.getDomain());
        entity.setOrgId(dto.getOrgId());
        entity.setOrgName(dto.getOrgName());
    }
}
