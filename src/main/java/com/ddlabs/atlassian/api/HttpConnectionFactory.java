package com.ddlabs.atlassian.api;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;

public interface HttpConnectionFactory {
    /**
     * Creates a new HttpsURLConnection instance for the given URI.
     *
     * @param uri the URI to connect to
     * @param method the HTTP method (e.g., GET, POST)
     * @param mediaType the media type (e.g., application/json)
     * @return a new HttpsURLConnection instance
     * @throws IOException if an I/O error occurs
     */
    HttpsURLConnection createConnection(URI uri, String method, String mediaType) throws IOException;
}
