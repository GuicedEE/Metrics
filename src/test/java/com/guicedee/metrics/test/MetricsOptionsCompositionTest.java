package com.guicedee.metrics.test;

import com.guicedee.metrics.implementations.MetricsVertxConfigurator;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetricsOptionsCompositionTest {
    @Test void preservesPoolsAndBothEventBusAddresses() {
        var runtime = new VertxOptions().setEventLoopPoolSize(2).setWorkerPoolSize(7)
                .setEventBusOptions(new EventBusOptions().setHost("127.0.0.1").setPort(15801)
                        .setClusterPublicHost("127.0.0.2").setClusterPublicPort(15802));
        assertSame(runtime, new MetricsVertxConfigurator().options(runtime));
        assertEquals(2, runtime.getEventLoopPoolSize()); assertEquals(7, runtime.getWorkerPoolSize());
        assertEquals("127.0.0.1", runtime.getEventBusOptions().getHost());
        assertEquals("127.0.0.2", runtime.getEventBusOptions().getClusterPublicHost());
        assertEquals(15801, runtime.getEventBusOptions().getPort());
        assertEquals(15802, runtime.getEventBusOptions().getClusterPublicPort());
        assertNotNull(runtime.getMetricsOptions());
    }
}
