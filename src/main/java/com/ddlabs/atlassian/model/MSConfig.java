package com.ddlabs.atlassian.model;
import net.java.ao.Entity;
public interface MSConfig extends Entity {
    void setServerName(String serverName);
    void setDescription(String description); ;
    void setServerType(String serverType);
    void setDomain(String domain);
    void setClientSecret(String clientSecret);
    void setClientId(String clientId);
    void setSite(String site);
    void setOrgName (String orgName);
    void setOrgId (String orgId);
    void setRedirectUrl(String redirectUrl);
    void setCodeVerifier(String codeVerifier);
    void setCodeChallenge(String codeChallenge);
    void setAccessToken(String accessToken);
    void setRefreshToken(String refreshToken);
    void setAccessTokenExpiredIn(int expiredIn);
    void setTokenType(String tokenType);
    void setScope(String scope);
    String getServerName();
    String getDescription(); ;
    String getServerType();
    String getDomain();
    String getClientSecret();
    String getClientId();
    String getSite ();
    String getOrgName();
    String getOrgId ();
    String getRedirectUrl();
    String getCodeVerifier();
    String getCodeChallenge();
    String getAccessToken();
    String getRefreshToken();
    String getAccessTokenExpiredI();
    String getTokenType();

}
