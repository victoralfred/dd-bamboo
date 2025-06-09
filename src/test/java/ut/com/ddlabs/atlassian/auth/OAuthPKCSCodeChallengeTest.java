package ut.com.ddlabs.atlassian.auth;

import com.ddlabs.atlassian.oauth2.CodeVerifierAndChallengeProvider;
import junit.framework.TestCase;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertNotEquals;

public class OAuthPKCSCodeChallengeTest extends TestCase {

    public void testGenerateCodeVerifier() {
        String codeVerifier = CodeVerifierAndChallengeProvider.generateCodeVerifier();
        assertNotNull(codeVerifier);
        assertTrue(codeVerifier.length() >= 43 && codeVerifier.length() <= 128);
        assertTrue(codeVerifier.matches("^[A-Za-z0-9-._~]+$"));
    }
    public void testGenerateCodeChallenge() {
        String codeVerifier = "testCodeVerifier";
        String codeChallenge;
        try {
            codeChallenge = CodeVerifierAndChallengeProvider.generateCodeChallenge(codeVerifier);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(codeChallenge);
        assertFalse(codeChallenge.isEmpty());
        assertTrue(codeChallenge.matches("^[A-Za-z0-9-._~]+$"));
        assertNotEquals(codeVerifier, codeChallenge);
    }

}