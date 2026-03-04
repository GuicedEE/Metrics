package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * Net Server metrics.
 * Base name: {@code vertx.net.servers.<host>:<port>}
 */
@Getter
public enum NetServerMetrics {
    OPEN_NETSOCKETS("open-netsockets"),
    OPEN_NETSOCKETS_REMOTE("open-netsockets.%s"),
    CONNECTIONS("connections"),
    EXCEPTIONS("exceptions"),
    BYTES_READ("bytes-read"),
    BYTES_WRITTEN("bytes-written");

    private final String metricName;

    /**
     * Creates a net server metric with the given name.
     *
     * @param metricName the metric name pattern
     */
    NetServerMetrics(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Formats the metric name with the given arguments.
     *
     * @param args the format arguments
     * @return the formatted metric name
     */
    public String format(Object... args) {
        return String.format(metricName, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return metricName;
    }
}
