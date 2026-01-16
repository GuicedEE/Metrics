package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * Event bus metrics.
 * Base name: vertx.eventbus
 */
@Getter
public enum EventBusMetrics {
    HANDLERS("handlers"),
    HANDLERS_ADDRESS("handlers.%s"),
    MESSAGES_BYTES_READ("messages.bytes-read"),
    MESSAGES_BYTES_WRITTEN("messages.bytes-written"),
    MESSAGES_PENDING("messages.pending"),
    MESSAGES_PENDING_LOCAL("messages.pending-local"),
    MESSAGES_PENDING_REMOTE("messages.pending-remote"),
    MESSAGES_DISCARDED("messages.discarded"),
    MESSAGES_DISCARDED_LOCAL("messages.discarded-local"),
    MESSAGES_DISCARDED_REMOTE("messages.discarded-remote"),
    MESSAGES_RECEIVED("messages.received"),
    MESSAGES_RECEIVED_LOCAL("messages.received-local"),
    MESSAGES_RECEIVED_REMOTE("messages.received-remote"),
    MESSAGES_DELIVERED("messages.delivered"),
    MESSAGES_DELIVERED_LOCAL("messages.delivered-local"),
    MESSAGES_DELIVERED_REMOTE("messages.delivered-remote"),
    MESSAGES_SENT("messages.sent"),
    MESSAGES_SENT_LOCAL("messages.sent-local"),
    MESSAGES_SENT_REMOTE("messages.sent-remote"),
    MESSAGES_PUBLISHED("messages.published"),
    MESSAGES_PUBLISHED_LOCAL("messages.published-local"),
    MESSAGES_PUBLISHED_REMOTE("messages.published-remote"),
    MESSAGES_REPLY_FAILURES("messages.reply-failures");

    private final String metricName;

    EventBusMetrics(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Returns the formatted metric name if it contains placeholders.
     */
    public String format(Object... args) {
        return String.format(metricName, args);
    }

    @Override
    public String toString() {
        return metricName;
    }
}
