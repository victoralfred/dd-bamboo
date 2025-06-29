package com.ddlabs.atlassian.impl.events.localevents;

public class ServerDeletedEvent {
    private final String serverId;
    public ServerDeletedEvent(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return "ServerDeleted{" +
                "serverId='" + serverId + '\'' +
                '}';
    }
}
