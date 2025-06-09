package com.ddlabs.atlassian.util;

import com.ddlabs.atlassian.exception.ValidationException;
import com.ddlabs.atlassian.metrics.remote.MockedClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class ValidationUtilsTest {
    @Test
    void itShouldValidateNotNull() {
        // Given
        MockedClass mockedClass = new MockedClass();
        // When
        ValidationUtils.validateNotNull(mockedClass, "test");
        // Then
        assertNotNull(mockedClass);
    }
    @Test
     void itShouldValidateNull() {
        //Given
        assertThrows(ValidationException.class, () -> {
            MockedClass mockedClass = null;
            ValidationUtils.validateNotNull(mockedClass, "Failed with null value");
        });
    }
    @Test
    void itShouldValidateNotEmpty() {
        // Given
        // When
        // Then
    }

    @Test
    void itShouldTestValidateNotEmpty() {
        // Given
        // When
        // Then
    }

    @Test
    void itShouldValidateCondition() {
        // Given
        // When
        // Then
    }

    @Test
    void itShouldCheckNotNull() {
        // Given
        // When
        // Then
    }
}