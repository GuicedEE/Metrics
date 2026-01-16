package com.guicedee.metrics.enumerations;

import lombok.Getter;

/**
 * Vert.x core metrics.
 */
@Getter
public enum VertxMetrics {
    EVENT_LOOP_SIZE("event-loop-size"),
    WORKER_POOL_SIZE("worker-pool-size"),
    CLUSTER_HOST("cluster-host"),
    CLUSTER_PORT("cluster-port");

    private final String metricName;

    VertxMetrics(String metricName) {
        this.metricName = metricName;
    }

    @Override
    public String toString() {
        return metricName;
    }
}
