package com.ddlabs.atlassian.auth;

import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
@Component
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {
    @Override
    public HttpsURLConnection createConnection( URI uri, String method, String mediaType) throws IOException {
        return (HttpsURLConnection) uri.toURL().openConnection();
    }
}
