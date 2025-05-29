package com.ddlabs.atlassian.http;

import com.ddlabs.atlassian.exception.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultHttpClientTest {

    private DefaultHttpClient httpClient;
    
    @Mock
    private HttpsURLConnection mockConnection;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        httpClient = new DefaultHttpClient() {
            @Override
            protected HttpsURLConnection openConnection(String urlString, String method) {
                return mockConnection;
            }
        };
        
        // Setup mock connection
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Test Response".getBytes()));
        when(mockConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(mockConnection.getResponseCode()).thenReturn(200);
    }
    
    @Test
    public void testGetSuccess() throws Exception {
        String response = httpClient.get("https://test.com/api");
        
        assertEquals("Test Response", response);
        verify(mockConnection).setRequestMethod("GET");
    }
    
    @Test
    public void testGetWithQueryParams() throws Exception {
        String response = httpClient.get("https://test.com/api", "param1=value1&param2=value2");
        
        assertEquals("Test Response", response);
        verify(mockConnection).setRequestMethod("GET");
    }
    
    @Test
    public void testPostSuccess() throws Exception {
        String response = httpClient.post("https://test.com/api", "Test Body");
        
        assertEquals("Test Response", response);
        verify(mockConnection).setRequestMethod("POST");
        verify(mockConnection).setRequestProperty("Content-Type", "application/json");
        verify(mockConnection).setDoOutput(true);
    }
    
    @Test
    public void testPostWithContentType() throws Exception {
        String response = httpClient.post("https://test.com/api", "Test Body", "application/x-www-form-urlencoded");
        
        assertEquals("Test Response", response);
        verify(mockConnection).setRequestMethod("POST");
        verify(mockConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        verify(mockConnection).setDoOutput(true);
    }
    
    @Test(expected = ApiException.class)
    public void testHandleErrorResponse() throws Exception {
        when(mockConnection.getResponseCode()).thenReturn(400);
        when(mockConnection.getErrorStream()).thenReturn(new ByteArrayInputStream("Bad Request".getBytes()));
        
        httpClient.get("https://test.com/api");
    }
    
    @Test
    public void testPostWithObject() throws Exception {
        TestObject testObject = new TestObject("test", 123);
        String response = httpClient.post("https://test.com/api", testObject);
        
        assertEquals("Test Response", response);
        verify(mockConnection).setRequestMethod("POST");
        verify(mockConnection).setRequestProperty("Content-Type", "application/json");
        verify(mockConnection).setDoOutput(true);
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