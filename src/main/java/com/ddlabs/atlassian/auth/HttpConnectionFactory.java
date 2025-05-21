package com.ddlabs.atlassian.auth;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;

public interface HttpConnectionFactory {
    HttpsURLConnection createConnection(URI uri, String method, String mediaType) throws IOException;
}
