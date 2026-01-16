package com.guicedee.metrics.test;

import com.codahale.metrics.MetricRegistry;
import com.guicedee.client.IGuiceContext;
import com.guicedee.metrics.Match;
import com.guicedee.metrics.MetricsOptions;
import com.guicedee.metrics.enumerations.HttpServerMetrics;
import com.guicedee.vertx.spi.VertXPreStartup;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.dropwizard.MetricsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@MetricsOptions(
        baseName = "test-base",
        monitoredHttpServerUris = @Match(value = "/hello", type = Match.MatchType.EQUALS)
)
public class VertxServerMetricsTest {

    @Test
    public void testServerMetrics() throws Exception {
        IGuiceContext.instance().inject();
        
        // Vertx is a singleton, so we need to be careful.
        // It is likely already started by another test with test-base.
        Vertx vertx = VertXPreStartup.getVertx();
        
        // Start an HTTP server
        HttpServer server = vertx.createHttpServer();
        CompletableFuture<HttpServer> listenFuture = new CompletableFuture<>();
        server.requestHandler(req -> {
            req.response().setStatusCode(200).end("Hello World");
        }).listen(0).onComplete(res -> {
            if (res.succeeded()) {
                listenFuture.complete(res.result());
            } else {
                listenFuture.completeExceptionally(res.cause());
            }
        });

        HttpServer startedServer = listenFuture.get(10, TimeUnit.SECONDS);
        int port = startedServer.actualPort();

        // Make a request to the server
        HttpClient client = vertx.createHttpClient();
        CompletableFuture<Void> requestFuture = new CompletableFuture<>();
        client.request(HttpMethod.GET, port, "localhost", "/hello")
                .compose(req -> req.send().compose(resp -> {
                    if (resp.statusCode() == 200) {
                        return io.vertx.core.Future.succeededFuture();
                    } else {
                        return io.vertx.core.Future.failedFuture("Status code " + resp.statusCode());
                    }
                }))
                .onComplete(res -> {
                    if (res.succeeded()) {
                        requestFuture.complete(null);
                    } else {
                        requestFuture.completeExceptionally(res.cause());
                    }
                });

        requestFuture.get(10, TimeUnit.SECONDS);

        // Verify metrics
        MetricRegistry registry = IGuiceContext.get(MetricRegistry.class);
        
        boolean foundMetric = false;
        // Search in all metrics for something related to our server
        // Since it's on a dynamic port, let's look for "http.servers" and "connections" or "requests"
        for (String name : registry.getNames()) {
            if (name.contains("http.servers") && (name.contains("requests") || name.contains("connections") || name.contains("bytes"))) {
                foundMetric = true;
                com.codahale.metrics.Metric metric = registry.getMetrics().get(name);
                System.out.println("[DEBUG_LOG] Found server metric: " + name);
                if (metric instanceof com.codahale.metrics.Counter) {
                    System.out.println("[DEBUG_LOG]   Type: Counter, Value: " + ((com.codahale.metrics.Counter) metric).getCount());
                } else if (metric instanceof com.codahale.metrics.Meter) {
                    com.codahale.metrics.Meter meter = (com.codahale.metrics.Meter) metric;
                    System.out.println("[DEBUG_LOG]   Type: Meter, Count: " + meter.getCount() + ", Mean Rate: " + meter.getMeanRate());
                } else if (metric instanceof com.codahale.metrics.Timer) {
                    com.codahale.metrics.Timer timer = (com.codahale.metrics.Timer) metric;
                    System.out.println("[DEBUG_LOG]   Type: Timer, Count: " + timer.getCount() + ", Mean Rate: " + timer.getMeanRate() + ", Median: " + timer.getSnapshot().getMedian());
                } else if (metric instanceof com.codahale.metrics.Histogram) {
                    com.codahale.metrics.Histogram histogram = (com.codahale.metrics.Histogram) metric;
                    System.out.println("[DEBUG_LOG]   Type: Histogram, Count: " + histogram.getCount() + ", Max: " + histogram.getSnapshot().getMax());
                } else if (metric instanceof com.codahale.metrics.Gauge) {
                    System.out.println("[DEBUG_LOG]   Type: Gauge, Value: " + ((com.codahale.metrics.Gauge<?>) metric).getValue());
                }
            }
        }
        
        if (!foundMetric) {
             registry.getNames().forEach(name -> System.out.println("[DEBUG_LOG] Metric: " + name));
        }

        Assertions.assertTrue(foundMetric, "Should have found at least one HTTP server metric");

        // Stop the server
        CompletableFuture<Void> closeFuture = new CompletableFuture<>();
        startedServer.close().onComplete(res -> closeFuture.complete(null));
        closeFuture.get(10, TimeUnit.SECONDS);
    }
}
