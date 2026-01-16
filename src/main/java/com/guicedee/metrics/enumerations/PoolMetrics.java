package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * Pool metrics.
 * Base name: vertx.pools.<type>.<name>
 */
@Getter
public enum PoolMetrics {
    QUEUE_DELAY("queue-delay"),
    QUEUE_SIZE("queue-size"),
    USAGE("usage"),
    IN_USE("in-use"),
    POOL_RATIO("pool-ratio"),
    MAX_POOL_SIZE("max-pool-size");

    private final String metricName;

    PoolMetrics(String metricName) {
        this.metricName = metricName;
    }

    @Override
    public String toString() {
        return metricName;
    }
}
