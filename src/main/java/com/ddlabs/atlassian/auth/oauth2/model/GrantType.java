package com.ddlabs.atlassian.auth.oauth2.model;

/**
 * Enum representing OAuth2 grant types.
 */
public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password");
    
    private final String value;
    
    GrantType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static GrantType fromValue(String value) {
        for (GrantType grantType : GrantType.values()) {
            if (grantType.getValue().equals(value)) {
                return grantType;
            }
        }
        throw new IllegalArgumentException("Invalid grant type: " + value);
    }
}
