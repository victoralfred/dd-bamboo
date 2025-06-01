package com.ddlabs.atlassian.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostResolver {
    public static String getHost() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostName();
    }
}
