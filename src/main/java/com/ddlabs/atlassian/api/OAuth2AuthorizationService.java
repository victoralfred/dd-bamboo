package com.ddlabs.atlassian.api;

import org.jetbrains.annotations.NotNull;

public interface OAuth2AuthorizationService {
    /**
     * Builds the authorization URL for OAuth2 authentication.
     *
     * @param clientId          The client ID.
     * @param redirectUri       The redirect URI.
     * @param responseType      The response type (e.g., "code").
     * @param code_challenge    The code challenge.
     * @param codeChallengeMethod The code challenge method (e.g., "S256").
     * @return The authorization URL.
     */
    default String buildAuthorizationUrl(final String domain, final String clientId,
                                         final String redirectUri, final String responseType,
                                         final String code_challenge,
                                         final String codeChallengeMethod){
        final String OAUTH_ENDPOINT = "oauth2/v1/authorize";
        final String AUTH_ENDPOINT = domain + "/" + OAUTH_ENDPOINT;
        return AUTH_ENDPOINT +"?"+
                "redirect_uri=" + redirectUri +
                "&client_id=" + clientId +
                "&response_type=" + responseType +
                "&code_challenge=" + code_challenge +
                "&code_challenge_method=" + codeChallengeMethod;
    }
    /**
     * Constructs the URL parameters for the authorization code exchange.
     *
     * @param clientId     The client ID.
     * @param clientSecret The client secret.
     * @param redirectUri  The redirect URI.
     * @param code         The authorization code.
     * @param codeVerifier The code verifier.
     * @return The URL parameters as a string.
     */
    String constAuthorizationCodeForAccessTokenUrl(String clientId, String clientSecret,
                                                          String redirectUri, String code, String codeVerifier);
    /**
     * Exchanges the authorization code for an access token.
     *
     * @param redirectUri       The redirect URI.
     * @param clientId          The client ID.
     * @param clientSecret      The client secret.
     * @param grantType         The grant type (e.g., "authorization_code").
     * @param codeVerifier      The code verifier.
     * @param code              The authorization code.
     * @param tokenEndpoint     The endpoint of te authorization server
     * @return The access token response as a string.
     */
    String exchangeAuthorizationCodeForAccessToken(String redirectUri, String clientId,
                                                   String clientSecret, String grantType, String codeVerifier,
                                                   String code, String tokenEndpoint);

}
