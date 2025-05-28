package com.ddlabs.atlassian.auth;
import com.ddlabs.atlassian.metrics.model.MSConfig;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;
import java.beans.PropertyChangeListener;

public class MSConfigTest implements MSConfig {
    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private String tokenEndpoint;
    private long accessTokenExpiry;
    public void setClientId(String s) {
        this.clientId = s;
    }

    @Override
    public void setSite(String site) {

    }

    @Override
    public void setOrgName(String orgName) {

    }

    @Override
    public void setOrgId(String orgId) {

    }

    @Override
    public void setRedirectUrl(String redirectUrl) {

    }

    @Override
    public void setCodeVerifier(String codeVerifier) {

    }

    @Override
    public void setCodeChallenge(String codeChallenge) {

    }

    @Override
    public void setAccessToken(String accessToken) {

    }

    @Override
    public void setServerName(String serverName) {

    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public void setServerType(String serverType) {

    }

    @Override
    public void setDomain(String domain) {

    }

    public void setClientSecret(String s) {
        this.clientSecret = s;
    }

    public void setRefreshToken(String s) {
        this.refreshToken = s;
    }

    @Override
    public void setAccessTokenExpiry(Long accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;

    }

    @Override
    public void setTokenType(String tokenType) {

    }

    @Override
    public void setScope(String scope) {

    }

    @Override
    public void setCodeCreationTime(Long codeCreationTime) {

    }

    @Override
    public void setCodeExpirationTime(Long codeExpirationTime) {

    }

    @Override
    public void setEnabled(Boolean enabled) {

    }

    @Override
    public void setConfigured(Boolean configured) {

    }

    @Override
    public void setApiEndpoint(String apiEndpoint) {

    }

    @Override
    public void setOauthEndpoint(String oauthEndpoint) {

    }

    public void setTokenEndpoint(String url) {
        this.tokenEndpoint = url;
    }

    @Override
    public String getServerName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getServerType() {
        return "";
    }

    @Override
    public String getDomain() {
        return "";
    }

    @Override
    public String getClientSecret() {
        return "";
    }

    @Override
    public String getClientId() {
        return "";
    }

    @Override
    public String getSite() {
        return "";
    }

    @Override
    public String getOrgName() {
        return "";
    }

    @Override
    public String getOrgId() {
        return "";
    }

    @Override
    public String getRedirectUrl() {
        return "";
    }

    @Override
    public String getCodeVerifier() {
        return "";
    }

    @Override
    public String getCodeChallenge() {
        return "";
    }

    @Override
    public String getAccessToken() {
        return "";
    }

    @Override
    public String getRefreshToken() {
        return "";
    }

    @Override
    public Long getAccessTokenExpiry() {
        return 0L;
    }

    @Override
    public String getTokenType() {
        return "";
    }

    @Override
    public Long getCodeCreationTime() {
        return 0L;
    }

    @Override
    public Long getCodeExpirationTime() {
        return 0L;
    }

    @Override
    public Boolean getEnabled() {
        return null;
    }

    @Override
    public Boolean getConfigured() {
        return null;
    }

    @Override
    public String getApiEndpoint() {
        return "";
    }

    @Override
    public String getOauthEndpoint() {
        return "";
    }

    @Override
    public String getTokenEndpoint() {
        return "";
    }

    @Override
    public String getScope() {
        return "";
    }

    public void setAccessTokenExpiry(long epochSecond) {
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init() {

    }

    @Override
    public void save() {

    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    public <X extends RawEntity<Integer>> Class<X> getEntityType() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }
}
