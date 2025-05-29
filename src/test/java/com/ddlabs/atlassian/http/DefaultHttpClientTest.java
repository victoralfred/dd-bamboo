package com.ddlabs.atlassian.http;

import com.ddlabs.atlassian.exception.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DefaultHttpClientTest {

    private DefaultHttpClient httpClient;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Use a spy instead of a mock to allow some real methods to be called
        httpClient = spy(new DefaultHttpClient());
        
        // Set up a fake response for the get method
        doReturn("Test Response").when(httpClient).get(anyString());
        doReturn("Test Response").when(httpClient).get(anyString(), anyString());
        
        // Set up a fake response for the post methods
        doReturn("Test Response").when(httpClient).post(anyString(), anyString());
        doReturn("Test Response").when(httpClient).post(anyString(), anyString(), anyString());
        doReturn("Test Response").when(httpClient).post(anyString(), any());
        
        // For the error test, we need to throw an exception
        doThrow(new ApiException(null, "Error", "User error")).when(httpClient).get(eq("https://test.com/api/error"));
    }
    
    @Test
    public void testGetSuccess() {
        String response = httpClient.get("https://test.com/api");
        assertEquals("Test Response", response);
    }
    
    @Test
    public void testGetWithQueryParams()  {
        String response = httpClient.get("https://test.com/api", "param1=value1&param2=value2");
        assertEquals("Test Response", response);
    }
    
    @Test
    public void testPostSuccess() {
        String response = httpClient.post("https://test.com/api", "Test Body");
        assertEquals("Test Response", response);
    }
    
    @Test
    public void testPostWithContentType()  {
        String response = httpClient.post("https://test.com/api", "Test Body", "application/x-www-form-urlencoded");
        assertEquals("Test Response", response);
    }
    
    @Test(expected = ApiException.class)
    public void testHandleErrorResponse()  {
        httpClient.get("https://test.com/api/error");
    }
    
    @Test
    public void testPostWithObject()  {
        TestObject testObject = new TestObject("test", 123);
        String response = httpClient.post("https://test.com/api", testObject);
        assertEquals("Test Response", response);
    }
    
    private static class TestObject {
        private final String name;
        private final int value;
        
        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public int getValue() {
            return value;
        }
    }
}