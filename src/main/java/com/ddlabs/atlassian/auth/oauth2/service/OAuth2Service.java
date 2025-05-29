package com.ddlabs.atlassian.auth.oauth2.service;

import com.ddlabs.atlassian.auth.oauth2.model.GrantType;
import com.ddlabs.atlassian.auth.oauth2.model.OAuth2Configuration;
import com.ddlabs.atlassian.auth.oauth2.model.OAuth2TokenResponse;
import com.ddlabs.atlassian.exception.AuthenticationException;

/**
 * Service interface for OAuth2 operations.
 */
public interface OAuth2Service {
    
    /**
     * Generates an authorization URL for the OAuth2 flow.
     *
     * @param config The OAuth2 configuration
     * @return The authorization URL
     * @throws AuthenticationException If an error occurs
     */
    String generateAuthorizationUrl(OAuth2Configuration config) throws AuthenticationException;
    
    /**
     * Exchanges an authorization code for an access token.
     *
     * @param code The authorization code
     * @param config The OAuth2 configuration
     * @return The OAuth2 token response
     * @throws AuthenticationException If an error occurs
     */
    OAuth2TokenResponse exchangeCodeForTokens(String code, OAuth2Configuration config) throws AuthenticationException;
    
    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token
     * @param config The OAuth2 configuration
     * @return The OAuth2 token response
     * @throws AuthenticationException If an error occurs
     */
    OAuth2TokenResponse refreshAccessToken(String refreshToken, OAuth2Configuration config) throws AuthenticationException;
    
    /**
     * Checks if an access token is expired.
     *
     * @param accessTokenExpiry The access token expiry timestamp
     * @return True if the token is expired, false otherwise
     */
    boolean isAccessTokenExpired(long accessTokenExpiry);
    
    /**
     * Checks if a token response is expired.
     *
     * @param tokenResponse The OAuth2 token response
     * @return True if the token is expired, false otherwise
     */
    boolean isTokenExpired(OAuth2TokenResponse tokenResponse);
}
