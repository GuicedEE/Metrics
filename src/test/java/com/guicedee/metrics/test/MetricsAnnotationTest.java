package com.guicedee.metrics.test;

import com.codahale.metrics.MetricRegistry;
import com.guicedee.client.IGuiceContext;
import com.guicedee.metrics.Match;
import com.guicedee.metrics.MetricsOptions;
import com.guicedee.vertx.spi.VertXPreStartup;
import io.vertx.core.Vertx;
import io.vertx.ext.dropwizard.MetricsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MetricsOptions(
        baseName = "test-base",
        monitoredEventBusHandlers = @Match(value = "test-address", type = Match.MatchType.EQUALS),
        monitoredHttpServerUris = @Match(value = "/test/.*", type = Match.MatchType.REGEX, alias = "test-uri")
)
public class MetricsAnnotationTest {

    @Test
    public void testMetricsAnnotations() throws Exception {
        IGuiceContext.instance().inject();
        // Force Vertx to start
        Vertx vertx = VertXPreStartup.getVertx();
        MetricsService metricsService = MetricsService.create(vertx);
        Assertions.assertNotNull(metricsService);
        // Assertions.assertEquals("test-base", metricsService.getBaseName(vertx));

        TestService service = IGuiceContext.get(TestService.class);

        service.countedMethod();
        service.timedMethod();
        service.meteredMethod();
        
        long val1 = service.metricMethod("dynamicName");
        Assertions.assertEquals(1, val1);
        long val2 = service.metricMethod("dynamicName");
        Assertions.assertEquals(2, val2);

        long explicitVal1 = service.explicitMetricMethod("suffix");
        Assertions.assertEquals(1, explicitVal1);
        long explicitVal2 = service.explicitMetricMethod("suffix");
        Assertions.assertEquals(2, explicitVal2);

        MetricRegistry registry = IGuiceContext.get(MetricRegistry.class);
        
        registry.getMetrics().forEach((name, metric) -> {
            System.out.println("[DEBUG_LOG] Metric: " + name);
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
        });

        String countedName = TestService.class.getName() + ".countedMethod";
        String timedName = TestService.class.getName() + ".timedMethod";
        String meteredName = "meteredMethod";

        Assertions.assertTrue(registry.getCounters().containsKey(countedName), "Counter " + countedName + " not found. Found: " + registry.getCounters().keySet());
        Assertions.assertEquals(1, registry.getCounters().get(countedName).getCount());

        Assertions.assertTrue(registry.getTimers().containsKey(timedName), "Timer " + timedName + " not found. Found: " + registry.getTimers().keySet());
        Assertions.assertEquals(1, registry.getTimers().get(timedName).getCount());

        Assertions.assertTrue(registry.getCounters().containsKey(meteredName), "Meter (Counter) " + meteredName + " not found. Found: " + registry.getCounters().keySet());
        Assertions.assertEquals(1, registry.getCounters().get(meteredName).getCount());

        Assertions.assertTrue(registry.getCounters().containsKey("dynamicName"));
        Assertions.assertEquals(2, registry.getCounters().get("dynamicName").getCount());

        Assertions.assertTrue(registry.getCounters().containsKey("explicitMetric.suffix"));
        Assertions.assertEquals(2, registry.getCounters().get("explicitMetric.suffix").getCount());

        service.customMethod();
        Assertions.assertTrue(CustomMetricInterceptor.called);
    }
}
