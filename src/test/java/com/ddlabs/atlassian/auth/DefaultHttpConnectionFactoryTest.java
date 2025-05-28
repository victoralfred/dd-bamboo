package com.ddlabs.atlassian.auth;

import junit.framework.TestCase;
import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import static org.mockito.Mockito.*;

public class DefaultHttpConnectionFactoryTest extends TestCase {
    private DefaultHttpConnectionFactory factory;

    public void setUp() {
        factory = new DefaultHttpConnectionFactory();
    }
    public void testOpenHttpsConnectionWithPostRequestWritesBody() throws Exception {
        // GIVEN
        URI uri = new URI("https://example.com/api");
        String method = "POST";
        String body = "{\"name\":\"test\"}";
        String mediaType = "application/json";
        HttpsURLConnection mockConnection = mock(HttpsURLConnection.class);
        OutputStream mockOutputStream = new ByteArrayOutputStream();
        // WHEN
        // Spy on URL to return our mock connection
        URL mockUrl = mock(URL.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        URI spyUri = spy(uri);
        doReturn(mockUrl).when(spyUri).toURL();
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        // THEN
        HttpURLConnection connection = factory.openHttpsConnection(spyUri, body, method, mediaType);
        verify(mockConnection).setRequestMethod(method);
        verify(mockConnection).setRequestProperty("Content-Type", mediaType);
        verify(mockConnection).setDoOutput(true);
        verify(mockConnection).getOutputStream();
        assertSame(mockConnection, connection);
    }
    public void testOpenHttpsConnectionWithGetRequestDoesNotWriteBody() throws Exception {
        // GIVEN
        URI uri = new URI("https://example.com/api");
        String method = "GET";
        String body = "";
        String mediaType = "application/json";
        // WHEN
        HttpsURLConnection mockConnection = mock(HttpsURLConnection.class);
        URL mockUrl = mock(URL.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        URI spyUri = spy(uri);
        doReturn(mockUrl).when(spyUri).toURL();
        // THEN
        HttpURLConnection connection = factory.openHttpsConnection(spyUri, body, method, mediaType);
        verify(mockConnection).setRequestMethod(method);
        verify(mockConnection).setRequestProperty("Content-Type", mediaType);
        verify(mockConnection, never()).getOutputStream();
        assertSame(mockConnection, connection);
    }

}