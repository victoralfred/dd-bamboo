package com.ddlabs.atlassian.util;

import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class HostResolverTest {

    @Test
    void itShouldGetHost() {
        // Given
        try {
            // When
            String hostName = HostResolver.getHost();
            // Then
            assertNotNull(hostName);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}