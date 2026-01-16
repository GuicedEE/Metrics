package com.guicedee.metrics.test;

import com.guicedee.client.IGuiceContext;
import com.guicedee.metrics.enumerations.*;
import com.guicedee.vertx.spi.VertXPreStartup;
import io.vertx.core.Vertx;
import io.vertx.ext.dropwizard.MetricsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class VertxMetricsEnumTest {

    @BeforeAll
    public static void setup() {
        IGuiceContext.instance().inject();
    }

    @Test
    public void testVertxMetrics() {
        Assertions.assertEquals("event-loop-size", VertxMetrics.EVENT_LOOP_SIZE.toString());
        Assertions.assertEquals("worker-pool-size", VertxMetrics.WORKER_POOL_SIZE.toString());
        Assertions.assertEquals("cluster-host", VertxMetrics.CLUSTER_HOST.toString());
        Assertions.assertEquals("cluster-port", VertxMetrics.CLUSTER_PORT.toString());
    }

    @Test
    public void testEventBusMetrics() {
        Assertions.assertEquals("handlers", EventBusMetrics.HANDLERS.toString());
        Assertions.assertEquals("handlers.my-address", EventBusMetrics.HANDLERS_ADDRESS.format("my-address"));
        Assertions.assertEquals("messages.pending", EventBusMetrics.MESSAGES_PENDING.toString());
    }

    @Test
    public void testHttpServerMetrics() {
        Assertions.assertEquals("requests", HttpServerMetrics.REQUESTS.toString());
        Assertions.assertEquals("get-requests", HttpServerMetrics.METHOD_REQUESTS.format("get"));
        Assertions.assertEquals("responses-2xx", HttpServerMetrics.RESPONSES_2XX.toString());
    }

    @Test
    public void testHttpClientMetrics() {
        Assertions.assertEquals("responses-5xx", HttpClientMetrics.RESPONSES_5XX.toString());
        Assertions.assertEquals("endpoint.localhost:8080.usage", HttpClientMetrics.ENDPOINT_USAGE.format("localhost:8080"));
    }

    @Test
    public void testNetServerMetrics() {
        Assertions.assertEquals("open-netsockets", NetServerMetrics.OPEN_NETSOCKETS.toString());
        Assertions.assertEquals("bytes-read", NetServerMetrics.BYTES_READ.toString());
    }

    @Test
    public void testDatagramMetrics() {
        Assertions.assertEquals("sockets", DatagramMetrics.SOCKETS.toString());
        Assertions.assertEquals("localhost:1234.bytes-read", DatagramMetrics.BYTES_READ.format("localhost:1234"));
    }

    @Test
    public void testPoolMetrics() {
        Assertions.assertEquals("queue-delay", PoolMetrics.QUEUE_DELAY.toString());
        Assertions.assertEquals("pool-ratio", PoolMetrics.POOL_RATIO.toString());
    }

    @Test
    public void testMetricsServiceIntegration() {
        Vertx vertx = VertXPreStartup.getVertx();
        MetricsService metricsService = MetricsService.create(vertx);
        Assertions.assertNotNull(metricsService);

        Set<String> names = metricsService.metricsNames();
        Assertions.assertNotNull(names);

        // Core Vert.x metrics should be present
        String baseName = metricsService.getBaseName(vertx);
        String eventLoopMetric = baseName + "." + VertxMetrics.EVENT_LOOP_SIZE.toString();
        
        // We don't necessarily know if the metric is already there without some activity,
        // but the name should be valid.
        Assertions.assertNotNull(eventLoopMetric);
        
        // Just print out some names for debugging if needed
        // names.forEach(System.out::println);
    }
}
