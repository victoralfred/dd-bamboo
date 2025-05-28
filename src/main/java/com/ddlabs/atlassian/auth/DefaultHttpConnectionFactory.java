package com.ddlabs.atlassian.auth;

import com.ddlabs.atlassian.api.HttpConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;

import static com.ddlabs.atlassian.model.ApplicationProperties.CONNECTION_TIMEOUT;
import static com.ddlabs.atlassian.model.ApplicationProperties.READ_TIMEOUT;

@Component
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {
    @Override
    public HttpURLConnection openHttpsConnection(@NotNull URI uri,
                                                 @NotNull String requestBody,
                                                 @NotNull String method,
                                                 @NotNull String mediaType)
            throws IOException {
        HttpsURLConnection connection = createConnection(uri);
        configureConnection(connection, method, mediaType);
        writeRequestedBodyIfNeeded(connection,method,requestBody.getBytes());
        return connection;
    }
    private void writeRequestedBodyIfNeeded(HttpsURLConnection connection,
                                            @NotNull String method, byte[] requestBytes) throws IOException {
        if(requiresRequestBody(method)){
            connection.setDoOutput(true);
            try(OutputStream os = connection.getOutputStream()){
                os.write(requestBytes);
            }
        }
    }
    private void configureConnection(HttpsURLConnection connection,
                                     @NotNull String method,
                                     @NotNull String mediaType) throws ProtocolException {
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", mediaType);
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

    }
    private boolean requiresRequestBody(String method) {
       return method.equals("POST") || method.equals("PUT") || method.equals("PATCH");
    }
}
