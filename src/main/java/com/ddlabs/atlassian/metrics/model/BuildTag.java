package com.ddlabs.atlassian.metrics.model;

public class BuildTag{
    private final String buildKey;
    private final String buildNumber;
    private final String result;

    public BuildTag(String buildKey, String buildNumber, String result) {
        this.buildKey = buildKey;
        this.buildNumber = buildNumber;
        this.result = result;
    }
    public String getBuildKey() {
        return buildKey;
    }
    public String getBuildNumber() {
        return buildNumber;
    }
    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"build_key\": \"" + buildKey + "\",\n" +
                "  \"build_number\": \"" + buildNumber + "\"\n" +
                "  \"build_status\": \"" + result + "\"\n" +
                "}";
    }
}
