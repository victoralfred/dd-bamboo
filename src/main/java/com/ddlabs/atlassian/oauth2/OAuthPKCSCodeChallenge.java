package com.ddlabs.atlassian.oauth2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * This class provides methods to generate a code verifier and a code challenge
 * for OAuth 2.0 PKCE (Proof Key for Code Exchange) flow.
 */
public class OAuthPKCSCodeChallenge {
   /**
     * Generates a random code verifier using SecureRandom.
     * The code verifier is a high-entropy cryptographic random string.
     *
     * @return A base64 URL encoded string representing the code verifier.
    */
  public static String generateCodeVerifier() {
      SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return base64UrlEncode(randomBytes);
  }
  /**
     * Encodes the input byte array to a base64 URL encoded string.
     * The URL encoding replaces '+' with '-', '/' with '_', and removes '=' padding.
     *
     * @param input The byte array to encode.
     * @return A base64 URL encoded string.
   */
  private static String base64UrlEncode(byte[] input) {
    String base64 = Base64.getEncoder().encodeToString(input);
    return base64.replace("+", "-").replace("/", "_").replace("=", "");
  }
  /**
     * Generates a code challenge from the code verifier using SHA-256 hashing.
     * The code challenge is used in the OAuth 2.0 PKCE flow to enhance security.
     *
     * @param codeVerifier The code verifier to hash.
     * @return A base64 URL encoded string representing the code challenge.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
   */
  public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(codeVerifier.getBytes());
    return base64UrlEncode(hash);
  }
}
