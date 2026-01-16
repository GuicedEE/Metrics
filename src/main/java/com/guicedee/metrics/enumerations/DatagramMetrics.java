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

    DatagramMetrics(String metricName) {
        this.metricName = metricName;
    }

    public String format(Object... args) {
        return String.format(metricName, args);
    }

    @Override
    public String toString() {
        return metricName;
    }
}
