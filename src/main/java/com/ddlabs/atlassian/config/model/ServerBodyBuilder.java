package com.ddlabs.atlassian.config.model;

import org.springframework.stereotype.Component;
import java.util.function.Function;
@Component
public class ServerBodyBuilder implements Function<ServerConfigBody,ServerConfigProperties> {
    @Override
    public ServerConfigProperties apply(ServerConfigBody serverConfigBody) {
        return new ServerConfigProperties(
                serverConfigBody.getTokenEndpoint(),
                serverConfigBody.getOauthEndpoint(),
                serverConfigBody.getApiEndpoint(),
                serverConfigBody.getClientSecret(),
                serverConfigBody.getClientKey(),
                null, null, null, null,
                serverConfigBody.getRedirectUrl(),
                serverConfigBody.getServerType(),
                serverConfigBody.getDescription(),
                serverConfigBody.getServerName());
    }
}
