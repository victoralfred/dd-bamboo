package com.ddlabs.atlassian.data.entity;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

/**
 * Entity for server configuration in ActiveObjects.
 */
@Preload
@Table("MS_CONFIG")
public interface MSConfigEntity extends Entity {
    String getServerType();
    void setServerType(String serverType);
    
    String getServerName();
    void setServerName(String serverName);
    
    String getDescription();
    void setDescription(String description);
    
    String getClientId();
    void setClientId(String clientId);
    
    @StringLength(StringLength.UNLIMITED)
    String getClientSecret();
    void setClientSecret(String clientSecret);
    
    String getRedirectUrl();
    void setRedirectUrl(String redirectUrl);
    
    @StringLength(StringLength.UNLIMITED)
    String getCodeVerifier();
    void setCodeVerifier(String codeVerifier);
    
    @StringLength(StringLength.UNLIMITED)
    String getCodeChallenge();
    void setCodeChallenge(String codeChallenge);
    
    long getCodeCreationTime();
    void setCodeCreationTime(long codeCreationTime);
    
    long getCodeExpirationTime();
    void setCodeExpirationTime(long codeExpirationTime);
    
    @StringLength(StringLength.UNLIMITED)
    String getAccessToken();
    void setAccessToken(String accessToken);
    
    @StringLength(StringLength.UNLIMITED)
    String getRefreshToken();
    void setRefreshToken(String refreshToken);
    
    String getTokenType();
    void setTokenType(String tokenType);
    
    String getScope();
    void setScope(String scope);
    
    long getAccessTokenExpiry();
    void setAccessTokenExpiry(long accessTokenExpiry);
    
    boolean isConfigured();
    void setConfigured(boolean configured);
    
    boolean isEnabled();
    void setEnabled(boolean enabled);
    
    String getApiEndpoint();
    void setApiEndpoint(String apiEndpoint);
    
    String getOauthEndpoint();
    void setOauthEndpoint(String oauthEndpoint);
    
    String getTokenEndpoint();
    void setTokenEndpoint(String tokenEndpoint);
    
    String getSite();
    void setSite(String site);
    
    String getDomain();
    void setDomain(String domain);
    
    String getOrgId();
    void setOrgId(String orgId);
    
    String getOrgName();
    void setOrgName(String orgName);
}
