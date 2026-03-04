package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * HTTP Server metrics.
 * Base name: {@code vertx.http.servers.<host>:<port>}
 * Includes Net Server metrics plus these.
 */
@Getter
public enum HttpServerMetrics {
    // Inherited from Net Server conceptually
    OPEN_NETSOCKETS("open-netsockets"),
    OPEN_NETSOCKETS_REMOTE("open-netsockets.%s"),
    CONNECTIONS("connections"),
    EXCEPTIONS("exceptions"),
    BYTES_READ("bytes-read"),
    BYTES_WRITTEN("bytes-written"),

    // HTTP specific
    REQUESTS("requests"),
    METHOD_REQUESTS("%s-requests"),
    METHOD_REQUESTS_PATH("%s-requests.%s"),
    RESPONSES_1XX("responses-1xx"),
    RESPONSES_2XX("responses-2xx"),
    RESPONSES_3XX("responses-3xx"),
    RESPONSES_4XX("responses-4xx"),
    RESPONSES_5XX("responses-5xx"),
    OPEN_WEBSOCKETS("open-websockets"),
    OPEN_WEBSOCKETS_REMOTE("open-websockets.%s");

    private final String metricName;

    /**
     * Creates an HTTP server metric with the given name.
     *
     * @param metricName the metric name pattern
     */
    HttpServerMetrics(String metricName) {
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
