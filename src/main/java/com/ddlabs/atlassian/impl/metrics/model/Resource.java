package com.ddlabs.atlassian.impl.metrics.model;

public class Resource implements Data{
    private final String name;
    private final String type;
    public Resource(String type,String name) {
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    @Override
    public String toString() {
        return "{\n" +
                "  \"name\": \"" + escapeJson(name) + "\",\n" +
                "  \"type\": \"" + escapeJson(type) + "\"\n" +
                "}";
    }
}
