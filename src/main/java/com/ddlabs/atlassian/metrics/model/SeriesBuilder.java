package com.ddlabs.atlassian.metrics.model;

import com.ddlabs.atlassian.util.HelperUtil;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class SeriesBuilder {
    private static Logger log = LoggerFactory.getLogger(SeriesBuilder.class);
    public String buildSeries(String metric, String timestamp, String value,
                              String hostname, MetricType metricType, BuildTag buildTag) {
        HelperUtil.checkNotNull(metric, "Metric cannot be null");
        HelperUtil.checkNotNull(timestamp, "Timestamp cannot be null");
        HelperUtil.checkNotNull(value, "Value cannot be null");
        HelperUtil.checkNotNull(hostname, "Hostname cannot be null");
        HelperUtil.checkNotNull(metricType, "MetricType cannot be null");
        HelperUtil.checkNotNull(buildTag, "BuildTag cannot be null");
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
           HelperUtil.checkNotNull(series);
           return new SeriesData(List.of(series)).toString();
       }catch(Exception e) {
           LogUtils.logError(log,"Can not pass Null to the Service object", e);
           throw new RuntimeException("Can not pass Null to the Service object", e);
       }
    }
}
