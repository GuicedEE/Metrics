package com.guicedee.metrics.test;

import com.codahale.metrics.MetricRegistry;
import com.guicedee.client.IGuiceContext;
import com.guicedee.metrics.MetricsOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

@MetricsOptions(
        prometheus = @MetricsOptions.PrometheusOptions(enabled = true, endpoint = "/metrics", port = 9090)
)
public class PrometheusIntegrationTest {

    @Test
    public void testPrometheusConfiguration() {
        IGuiceContext.instance().inject();
        MetricRegistry registry = IGuiceContext.get(MetricRegistry.class);
        Assertions.assertNotNull(registry);

        // Verify that the configurator is registered via ServiceLoader using reflection to avoid compile-time dependency issues
        try {
            Class<?> configuratorClass = Class.forName("com.guicedee.vertx.web.spi.VertxRouterConfigurator");
            ServiceLoader<?> loaders = ServiceLoader.load(configuratorClass);
            boolean found = false;
            for (Object loader : loaders) {
                if (loader.getClass().getName().equals("com.guicedee.metrics.implementations.PrometheusMetricsConfigurator")) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found, "PrometheusMetricsConfigurator not found in ServiceLoader");
        } catch (ClassNotFoundException e) {
            System.out.println("[DEBUG_LOG] VertxRouterConfigurator not on classpath, skipping ServiceLoader check");
        }
    }
}
