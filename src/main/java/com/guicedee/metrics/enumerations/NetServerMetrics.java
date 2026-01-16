package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * Net Server metrics.
 * Base name: vertx.net.servers.<host>:<port>
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

    NetServerMetrics(String metricName) {
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
