package com.ddlabs.atlassian.dao.adapter.dto;

import com.ddlabs.atlassian.dao.adapter.entity.MSConfigEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between MSConfigEntity and ServerConfigBuilder.
 */
@Component
public class ServerConfigMapper {

    public void transForForAuthTokenRequest(ServerConfigBuilder configDTO, String serverType, String serverName,
                                            String description, String clientId, String clientSecret,
                                            String redirectUrl, String codeVerifier,
                                            String codeChallenge, Long codeCreationTime, Long codeExpirationTime,
                                            String apiEndpoint, String oauthEndpoint, String tokenEndpoint) {
        transFormObject(configDTO, serverType, serverName, description, clientId, clientSecret, redirectUrl, codeVerifier, codeChallenge,
                codeCreationTime, codeExpirationTime, apiEndpoint, oauthEndpoint, tokenEndpoint);
    }

    private static void transFormObject(ServerConfigBuilder configDTO, String serverType, String serverName, String description,
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
     * Converts an MSConfigEntity to a ServerConfigBuilder.
     *
     * @param entity The entity to convert
     * @return The DTO
     */
    public ServerConfigBuilder toDtoForAccessTokenCreation(ServerConfigBuilder entity) {
        if (entity == null) {
            return null;
        }
        ServerConfigBuilder dto = new ServerConfigBuilder();
        dtoObjectTransformer(dto, entity.getServerType(), entity.getServerName(), entity.getDescription(),
                entity.getClientId(), entity.getClientSecret(), entity.getRedirectUrl(),
                entity.getCodeVerifier(), entity.getCodeChallenge(), entity.getCodeCreationTime(),
                entity.getCodeExpirationTime(), entity.getApiEndpoint(), entity.getOauthEndpoint(),
                entity.getTokenEndpoint(), entity.getSite(), entity.getDomain(), entity.getOrgId(),
                entity.getOrgName(), entity.isConfigured(), entity.isEnabled(),
                entity.getAccessToken(), entity.getRefreshToken(), entity.getTokenType(),
                entity.getScope(), entity.getAccessTokenExpiry());
        dto.setTokenEndpoint(entity.getTokenEndpoint()); // todo refactor how to handle this
        return dto;
    }

    public void dtoObjectTransformer(ServerConfigBuilder dto, String serverType, String serverName, String description, String clientId,
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
    public ServerConfigBuilder apply(MSConfigEntity entity) {
        if(entity==null){
            return new ServerConfigBuilder();
        }
        return new ServerConfigBuilder.ServerBuilder()
                .serverType(entity.getServerType())
                .serverName(entity.getServerName())
                .description(entity.getDescription())
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .redirectUrl(entity.getRedirectUrl())
                .codeVerifier(entity.getCodeVerifier())
                .codeChallenge(entity.getCodeChallenge())
                .codeCreationTime(entity.getCodeCreationTime())
                .codeExpirationTime(entity.getCodeExpirationTime())
                .apiEndpoint(entity.getApiEndpoint())
                .oauthEndpoint(entity.getOauthEndpoint())
                .tokenEndpoint(entity.getTokenEndpoint())
                .site(entity.getSite())
                .domain(entity.getDomain())
                .orgId(entity.getOrgId())
                .orgName(entity.getOrgName())
                .configured(entity.getConfigured())
                .enabled(entity.getEnabled())
                .accessToken(entity.getAccessToken())
                .refreshToken(entity.getRefreshToken())
                .tokenType(entity.getTokenType())
                .scope(entity.getScope())
                .accessTokenExpiry(entity.getAccessTokenExpiry()==null?0:entity.getAccessTokenExpiry())
                .build();
    }

}
