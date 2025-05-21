package com.ddlabs.atlassian.model;
import net.java.ao.Entity;
public interface MSConfig extends Entity {
    String getDomain(String domain);
    String getClientSecret(String clientSecret);
    String getClientId(String clientId);
    String getSite (String site);
    String getOrgName (String orgName);
    String getOrgId (String orgId);
    String getBambooRestApiUrl(String redirectUri);
}
