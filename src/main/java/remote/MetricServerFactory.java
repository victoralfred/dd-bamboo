package remote;

import com.google.api.Metric;

public interface MetricServerFactory {
    MetricServer getMetricServer(String provider);
}
