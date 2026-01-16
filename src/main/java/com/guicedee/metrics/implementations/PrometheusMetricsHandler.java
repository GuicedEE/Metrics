package com.guicedee.metrics.implementations;

import com.codahale.metrics.*;
import com.guicedee.client.IGuiceContext;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.SortedMap;

public class PrometheusMetricsHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        MetricRegistry registry = IGuiceContext.get(MetricRegistry.class);
        StringBuilder sb = new StringBuilder();

        // Gauges
        for (Map.Entry<String, Gauge> entry : registry.getGauges().entrySet()) {
            String name = sanitizeName(entry.getKey());
            Object value = entry.getValue().getValue();
            if (value instanceof Number) {
                appendMetric(sb, name, "gauge", null, value);
            }
        }

        // Counters
        for (Map.Entry<String, Counter> entry : registry.getCounters().entrySet()) {
            String name = sanitizeName(entry.getKey());
            appendMetric(sb, name, "counter", null, entry.getValue().getCount());
        }

        // Histograms
        for (Map.Entry<String, Histogram> entry : registry.getHistograms().entrySet()) {
            String name = sanitizeName(entry.getKey());
            Histogram histogram = entry.getValue();
            Snapshot snapshot = histogram.getSnapshot();
            
            appendMetric(sb, name + "_count", "summary", null, histogram.getCount());
            appendMetric(sb, name, "summary", "quantile=\"0.5\"", snapshot.getMedian());
            appendMetric(sb, name, "summary", "quantile=\"0.75\"", snapshot.get75thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.95\"", snapshot.get95thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.98\"", snapshot.get98thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.99\"", snapshot.get99thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.999\"", snapshot.get999thPercentile());
        }

        // Meters
        for (Map.Entry<String, Meter> entry : registry.getMeters().entrySet()) {
            String name = sanitizeName(entry.getKey());
            Meter meter = entry.getValue();
            appendMetric(sb, name + "_count", "counter", null, meter.getCount());
            appendMetric(sb, name + "_m1_rate", "gauge", null, meter.getOneMinuteRate());
            appendMetric(sb, name + "_m5_rate", "gauge", null, meter.getFiveMinuteRate());
            appendMetric(sb, name + "_m15_rate", "gauge", null, meter.getFifteenMinuteRate());
            appendMetric(sb, name + "_mean_rate", "gauge", null, meter.getMeanRate());
        }

        // Timers
        for (Map.Entry<String, Timer> entry : registry.getTimers().entrySet()) {
            String name = sanitizeName(entry.getKey());
            Timer timer = entry.getValue();
            Snapshot snapshot = timer.getSnapshot();
            
            appendMetric(sb, name + "_count", "summary", null, timer.getCount());
            appendMetric(sb, name, "summary", "quantile=\"0.5\"", snapshot.getMedian());
            appendMetric(sb, name, "summary", "quantile=\"0.75\"", snapshot.get75thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.95\"", snapshot.get95thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.98\"", snapshot.get98thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.99\"", snapshot.get99thPercentile());
            appendMetric(sb, name, "summary", "quantile=\"0.999\"", snapshot.get999thPercentile());
            
            appendMetric(sb, name + "_m1_rate", "gauge", null, timer.getOneMinuteRate());
            appendMetric(sb, name + "_m5_rate", "gauge", null, timer.getFiveMinuteRate());
            appendMetric(sb, name + "_m15_rate", "gauge", null, timer.getFifteenMinuteRate());
            appendMetric(sb, name + "_mean_rate", "gauge", null, timer.getMeanRate());
        }

        event.response()
                .putHeader("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
                .end(sb.toString());
    }

    private String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private void appendMetric(StringBuilder sb, String name, String type, String labels, Object value) {
        sb.append("# TYPE ").append(name).append(" ").append(type).append("\n");
        sb.append(name);
        if (labels != null && !labels.isEmpty()) {
            sb.append("{").append(labels).append("}");
        }
        sb.append(" ").append(value).append("\n");
    }
}
