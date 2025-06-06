package com.ddlabs.atlassian.impl.metrics.model;

public interface Data {
    default String escapeJson(String s) {
        if(s!=null){
            return s.replace("\"", "\\\"");
        }
        return "";
    }
}
