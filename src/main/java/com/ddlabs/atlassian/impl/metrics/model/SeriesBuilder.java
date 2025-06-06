package com.ddlabs.atlassian.impl.metrics.model;

import com.ddlabs.atlassian.util.LogUtils;
import com.ddlabs.atlassian.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class SeriesBuilder {
    private static final Logger log = LoggerFactory.getLogger(SeriesBuilder.class);
    public String buildSeries(String metric, String timestamp, String value,
                              String hostname, MetricType metricType, BuildTag buildTag) {
        ValidationUtils.checkNotNull(metric, "Metric cannot be null");
        ValidationUtils.checkNotNull(timestamp, "Timestamp cannot be null");
        ValidationUtils.checkNotNull(value, "Value cannot be null");
        ValidationUtils.checkNotNull(hostname, "Hostname cannot be null");
        ValidationUtils.checkNotNull(metricType, "MetricType cannot be null");
        ValidationUtils.checkNotNull(buildTag, "BuildTag cannot be null");
        Series series = new Series(
                metric,
                metricType,
                List.of(new Point(timestamp, value)),
                List.of(new Resource("host", hostname)),
                Arrays.asList("build_key:"+ buildTag.getBuildKey(),
                        "build_number:" + buildTag.getBuildNumber(),
                        "build_result:" + buildTag.getResult()
                ));
       try{
           ValidationUtils.checkNotNull(series);
           return new SeriesData(List.of(series)).toString();
       }catch(Exception e) {
           LogUtils.logError(log,"Can not pass Null to the Service object", e);
           throw new RuntimeException("Can not pass Null to the Service object", e);
       }
    }
}
