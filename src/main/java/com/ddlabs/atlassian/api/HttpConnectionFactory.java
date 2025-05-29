package com.ddlabs.atlassian.api;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public interface HttpConnectionFactory {
    /**
     * Opens an HTTPS connection to the specified URI with the given parameters.
     *
     * @param uri              The URI to connect to.
     * @param urlParameters    The URL parameters to send in the request body.
     * @param method           The HTTP method (e.g., "POST").
     * @param mediaType        The media type (e.g., "application/x-www-form-urlencoded").
     * @return The HttpsURLConnection object.
     * @throws IOException If an I/O error occurs.
     */
    HttpsURLConnection openHttpsConnection(URI uri, String urlParameters,
                                         String method, String mediaType) throws IOException;
     /**
     * Creates a new HttpsURLConnection instance for the given URI.
     *
     * @param uri the URI to connect to
     * @return a new HttpsURLConnection instance
     * @throws IOException if an I/O error occurs
     */
    default HttpsURLConnection createConnection(@NotNull final URI uri) throws IOException{
        return (HttpsURLConnection) uri.toURL().openConnection();
    }
    /**
     * Creates an HTTP connection to the specified URI with the given parameters.
     *
     * @param uri              The URI to connect to.
     * @param urlParameters    The URL parameters to send in the request body.
     * @param method           The HTTP method (e.g., "POST").
     * @param mediaType        The media type (e.g., "application/x-www-form-urlencoded").
     * @return The HttpURLConnection object.
     * @throws IOException If an I/O error occurs.
     */
    default HttpURLConnection getUrlConnection(@NotNull final URI uri,
                                               @NotNull final String urlParameters,
                                               @NotNull final String method,
                                               @NotNull final String mediaType) throws IOException {
        return openHttpsConnection(uri, urlParameters, method, mediaType);
    }

}
