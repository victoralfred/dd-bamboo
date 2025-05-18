package com.ddlabs.atlassian.auth;

import junit.framework.TestCase;


public class OAuth2AuthorizationTest extends TestCase {
    private final OAuth2Authorization oAuth2Authorization = new OAuth2Authorization();
    public void testBuildAuthorizationUrl() {
        String domain = "https://api.example.com";
        String clientId = "your_client_id";
        String redirectUri = "https://api.yourapp.com/callback";
        String responseType = "code";
        String code_challenge = "your_code_challenge";
        String codeChallengeMethod = "S256";

        String result = oAuth2Authorization.buildAuthorizationUrl(domain, clientId, redirectUri, responseType, code_challenge, codeChallengeMethod);
        assertNotNull(result);
        System.out.println("Authorization URL: " + result);
        assertTrue(result.startsWith(domain + "/oauth2/v1/authorize"));
    }
}