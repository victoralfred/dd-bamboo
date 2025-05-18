package com.ddlabs.atlassian.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class OAuthPKCSCodeChallenge {
    // Generate a cryptographically secure random string
  public static String generateCodeVerifier() {
      SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return base64UrlEncode(randomBytes);
  }
  // Base64 URL encode the input byte array (without padding)
  private static String base64UrlEncode(byte[] input) {
    String base64 = Base64.getEncoder().encodeToString(input);
    return base64.replace("+", "-").replace("/", "_").replace("=", "");
  }
  // Generates the code challenge from the code verifier using SHA-256
  public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(codeVerifier.getBytes());
    return base64UrlEncode(hash);
  }
}
