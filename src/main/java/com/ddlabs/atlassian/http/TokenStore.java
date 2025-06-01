package com.ddlabs.atlassian.http;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class TokenStore {
    private final Map<String, String> tokenMap = new ConcurrentHashMap<>();
    public void putToken(String key, String token) {
        tokenMap.put(key, token);
    }
    public String getToken(String key) {
        return tokenMap.get(key);
    }
    public void removeToken(String key) {
        tokenMap.remove(key);
    }
}
