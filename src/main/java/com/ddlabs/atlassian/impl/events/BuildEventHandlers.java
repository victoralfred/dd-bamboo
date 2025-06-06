package com.ddlabs.atlassian.impl.events;

import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.ddlabs.atlassian.impl.metrics.model.BuildTag;
import com.ddlabs.atlassian.impl.metrics.model.MetricType;
import com.ddlabs.atlassian.impl.metrics.model.SeriesBuilder;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BuildEventHandlers {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final SeriesBuilder seriesBuilder;
    private final ConcurrentMetricHandler concurrentMetricHandler;
    public BuildEventHandlers(SeriesBuilder seriesBuilder, ConcurrentMetricHandler concurrentMetricHandler) {
        this.seriesBuilder = seriesBuilder;
        this.concurrentMetricHandler = concurrentMetricHandler;
    }
    public void handleBuildEvent(Object event) {
        try{
            //String hostName = HostResolver.getHost();
            if (event instanceof BuildFinishedEvent buildFinishedEvent) {
                // Process the build finished event
                String seriesPoint = createSeriesPoint(
                        "bamboo.build.status",
                        buildFinishedEvent.getBuildCompletionTimestamp().getTime(),
                        "1.0",
                        "COMP-MMWCWVFQKC",
                        MetricType.COUNTER,new BuildTag(buildFinishedEvent.getPlanKey().getKey(),
                                String.valueOf(buildFinishedEvent.getBuildNumber()),
                                buildFinishedEvent.getBuildState().getState())
                );
                if(concurrentMetricHandler.addSeriesToQueue(seriesPoint)){
                    log.debug("Series point added to queue");

                }

            } else {
                LogUtils.logError(log, "Unexpected event type: " +event.getClass().getName(),
                        new RuntimeException("Unexpected event type"));

            }
        } catch (Exception e) {
            LogUtils.logError(log, "Error getting hostname " + e, e);
        }

    }



    private String createSeriesPoint(String metric, long timestamp, String value, String hostName,
                                     MetricType metricType, BuildTag metricTags) {
        return seriesBuilder.buildSeries(
                metric,
                String.valueOf(timestamp/1000),
                value,
                hostName,
                metricType,
                metricTags
        );
    }
}
