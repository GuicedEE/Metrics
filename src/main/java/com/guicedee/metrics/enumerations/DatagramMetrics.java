package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * Datagram socket metrics.
 * Base name: vertx.datagram
 */
@Getter
public enum DatagramMetrics {
    SOCKETS("sockets"),
    EXCEPTIONS("exceptions"),
    BYTES_WRITTEN("bytes-written"),
    BYTES_READ("%s.bytes-read");

    private final String metricName;

    /**
     * Creates a datagram metric with the given name.
     *
     * @param metricName the metric name pattern
     */
    DatagramMetrics(String metricName) {
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
