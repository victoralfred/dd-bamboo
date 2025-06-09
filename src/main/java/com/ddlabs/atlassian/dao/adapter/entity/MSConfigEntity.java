package com.ddlabs.atlassian.dao.adapter.entity;
import net.java.ao.Entity;
import net.java.ao.schema.StringLength;

public interface MSConfigEntity extends Entity {
    void setServerName(String serverName);
    String getServerName();

    void setDescription(String description);
    String getDescription();

    void setServerType(String serverType);
    String getServerType();

    void setDomain(String domain);
    String getDomain();

    @StringLength(StringLength.UNLIMITED)
    void setClientSecret(String clientSecret);
    String getClientSecret();

    void setClientId(String clientId);
    String getClientId();

    void setSite(String site);
    String getSite ();

    void setOrgName (String orgName);
    String getOrgName();

    void setOrgId (String orgId);
    String getOrgId ();

    void setRedirectUrl(String redirectUrl);
    String getRedirectUrl();

    @StringLength(StringLength.UNLIMITED)
    void setCodeVerifier(String codeVerifier);
    String getCodeVerifier();

    @StringLength(StringLength.UNLIMITED)
    void setCodeChallenge(String codeChallenge);
    String getCodeChallenge();

    @StringLength(StringLength.UNLIMITED)
    void setAccessToken(String accessToken);
    String getAccessToken();

    @StringLength(StringLength.UNLIMITED)
    void setRefreshToken(String refreshToken);
    String getRefreshToken();

    void setAccessTokenExpiry(Long accessTokenExpiry);
    Long getAccessTokenExpiry();

    void setTokenType(String tokenType);
    String getTokenType();

    void setScope(String scope);
    String getScope();

    void setCodeCreationTime(Long codeCreationTime);
    Long getCodeCreationTime();

    void setCodeExpirationTime(Long codeExpirationTime);
    Long getCodeExpirationTime();

    void setEnabled(Boolean enabled);
    Boolean getEnabled();

    void setConfigured(Boolean configured);
    Boolean getConfigured();

    void setApiEndpoint(String apiEndpoint);
    String getApiEndpoint();

    void setOauthEndpoint(String oauthEndpoint);
    String getOauthEndpoint();

    void setTokenEndpoint(String tokenEndpoint);
    String getTokenEndpoint();
}
