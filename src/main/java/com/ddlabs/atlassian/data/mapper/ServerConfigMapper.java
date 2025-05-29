package com.ddlabs.atlassian.data.mapper;

import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between MSConfigEntity and ServerConfigDTO.
 */
@Component
public class ServerConfigMapper {

    public void transForForAuthTokenRequest(ServerConfigDTO configDTO, String serverType, String serverName,
                                                   String description, String clientId, String clientSecret,
                                                   String redirectUrl, String codeVerifier,
                                                   String codeChallenge, Long codeCreationTime, Long codeExpirationTime,
                                                   String apiEndpoint, String oauthEndpoint, String tokenEndpoint) {
        transFormObject(configDTO, serverType, serverName, description, clientId, clientSecret, redirectUrl, codeVerifier, codeChallenge,
                codeCreationTime, codeExpirationTime, apiEndpoint, oauthEndpoint, tokenEndpoint);
    }

    private static void transFormObject(ServerConfigDTO configDTO, String serverType, String serverName, String description,
                                        String clientId, String clientSecret, String redirectUrl, String codeVerifier,
                                        String codeChallenge, Long codeCreationTime, Long codeExpirationTime,
                                        String apiEndpoint, String oauthEndpoint, String tokenEndpoint) {
        configDTO.setServerType(serverType);
        configDTO.setServerName(serverName);
        configDTO.setDescription(description);
        configDTO.setClientId(clientId);
        configDTO.setClientSecret(clientSecret);
        configDTO.setRedirectUrl(redirectUrl);
        configDTO.setCodeVerifier(codeVerifier);
        configDTO.setCodeChallenge(codeChallenge);
        configDTO.setCodeCreationTime(codeCreationTime);
        configDTO.setCodeExpirationTime(codeExpirationTime);
        configDTO.setApiEndpoint(apiEndpoint);
        configDTO.setOauthEndpoint(oauthEndpoint);
        configDTO.setTokenEndpoint(tokenEndpoint);
    }

    /**
     * Converts a MSConfigEntity to a ServerConfigDTO.
     *
     * @param entity The entity to convert
     * @return The DTO
     */
    public ServerConfigDTO toDtoForAccessTokenCreation(MSConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        ServerConfigDTO dto = new ServerConfigDTO();
        dtoObjectTransformer(dto, entity.getServerType(), entity.getServerName(), entity.getDescription(),
                entity.getClientId(), entity.getClientSecret(), entity.getRedirectUrl(),
                entity.getCodeVerifier(), entity.getCodeChallenge(), entity.getCodeCreationTime(),
                entity.getCodeExpirationTime(), entity.getApiEndpoint(), entity.getOauthEndpoint(),
                entity.getTokenEndpoint(), entity.getSite(), entity.getDomain(), entity.getOrgId(),
                entity.getOrgName(), entity.getConfigured(), entity.getEnabled(),
                entity.getAccessToken(), entity.getRefreshToken(), entity.getTokenType(),
                entity.getScope(), entity.getAccessTokenExpiry());
        dto.setTokenEndpoint(entity.getTokenEndpoint()); // todo refactor how to handle this
        return dto;
    }

    public static void dtoObjectTransformer(ServerConfigDTO dto, String serverType, String serverName, String description, String clientId,
                                            String clientSecret, String redirectUrl, String codeVerifier,
                                            String codeChallenge, Long codeCreationTime, Long codeExpirationTime,
                                            String apiEndpoint, String oauthEndpoint,
                                            String tokenEndpoint, String site, String domain, String orgId, String orgName
    , boolean configured, boolean enabled, String accessToken, String refreshToken, String tokenType, String scope, Long accessTokenExpiry) {
        transFormObject(dto, serverType, serverName, description, clientId, clientSecret, redirectUrl, codeVerifier, codeChallenge, codeCreationTime, codeExpirationTime, apiEndpoint, oauthEndpoint, tokenEndpoint);
        dto.setSite(site);
        dto.setDomain(domain);
        dto.setOrgId(orgId);
        dto.setOrgName(orgName);
        dto.setConfigured(configured);
        dto.setEnabled(enabled);
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        dto.setTokenType(tokenType);
        dto.setScope(scope);
        dto.setAccessTokenExpiry(accessTokenExpiry);
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
