package com.ddlabs.atlassian.impl.events;

import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.ddlabs.atlassian.impl.metrics.model.BuildTag;
import com.ddlabs.atlassian.impl.metrics.model.MetricType;
import com.ddlabs.atlassian.impl.metrics.model.SeriesBuilder;
import com.ddlabs.atlassian.util.HostResolver;
import com.ddlabs.atlassian.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The BambooEventHandlers class is responsible for handling and processing events
 * related to Bamboo build execution and completion. It integrates with various
 * components such as SeriesBuilder and EventMessagePusher to log, create metric data,
 * and push series points to a queue for further processing.
 *
 * This class primarily focuses on handling build finish events, extracting relevant
 * build information, creating series points, and queuing them for subsequent delivery
 * to an external metric system.
 */
@Component
public class BambooEventHandlers {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final SeriesBuilder seriesBuilder;
    private final EventMessagePusher eventMessagePusher;
    public BambooEventHandlers(SeriesBuilder seriesBuilder, EventMessagePusher eventMessagePusher) {
        this.seriesBuilder = seriesBuilder;
        this.eventMessagePusher = eventMessagePusher;
    }

    /**
     * Handles build events by processing build completion data and logging relevant information.
     * Specifically, this method processes instances of {@code BuildFinishedEvent}.
     * For a valid build completion event, it creates a series point and attempts
     * to queue the generated data, logging the outcome of the operation.
     * If an unexpected event type is received, it logs an error.
     * Any exception during hostname resolution or processing is also logged.
     *
     * @param event The event object to handle. Expected to be an instance of {@code BuildFinishedEvent}.
     *              If the event is not of the expected type, an error is logged*/
    public void handleBuildEvent(Object event) {
        try{
            String hostName = HostResolver.getHost();
            if (event instanceof BuildFinishedEvent buildFinishedEvent) {
                // Process the build finished event
                String seriesPoint = createSeriesPoint(
                        buildFinishedEvent.getBuildCompletionTimestamp().getTime(),
                        hostName,
                        new BuildTag(buildFinishedEvent.getBuildPlanKey(),
                                String.valueOf(buildFinishedEvent.getBuildNumber()),
                                buildFinishedEvent.getBuildState().getState())
                );
                if(eventMessagePusher.addSeriesToQueue(seriesPoint)){
                    log.info("Series point added to queue");
                }
            } else {
                LogUtils.logError(log, "Unexpected event type: " +event.getClass().getName(),
                        new RuntimeException("Unexpected event type"));
            }
        } catch (Exception e) {
            LogUtils.logError(log, "Error getting hostname " + e, e);
        }
    }

    /**
     * Creates a series point representation for a metric by utilizing the SeriesBuilder utility.
     * This method builds and formats the required data by combining metric information,
     * timestamp (converted to seconds), value, host details, metric type, and tagging information.
     *
     * @param timestamp  The timestamp corresponding to the metric, in milliseconds.
     *                   It will be converted to seconds internally.
     * @param hostName   The name of the host where the metric is generated. Cannot be null.
     * @param metricTags The tagging information associated with the metric, including build details. Cannot be null.
     * @return A string representation of the series point, formatted as per the SeriesBuilder implementation.
     */
    private String createSeriesPoint(long timestamp, String hostName,
                                     BuildTag metricTags) {
        return seriesBuilder.buildSeries(
                "bamboo.build.status",
                String.valueOf(timestamp/1000),
                "1.0",
                hostName,
                MetricType.COUNTER,
                metricTags
        );
    }
}
