package com.ddlabs.atlassian.auth;

import com.ddlabs.atlassian.api.HttpConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import static com.ddlabs.atlassian.model.ApplicationProperties.CONNECTION_TIMEOUT;
import static com.ddlabs.atlassian.model.ApplicationProperties.READ_TIMEOUT;

@Component
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {
    @Override
    public HttpURLConnection openHttpsConnection(@NotNull final URI uri,
                                                 @NotNull final  String urlParameters,
                                                 @NotNull final  String method,
                                                 @NotNull final  String mediaType)
            throws IOException {
        HttpsURLConnection connection = createConnection(uri);
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", mediaType);
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }
        return connection;
    }


}
