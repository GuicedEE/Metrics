package com.guicedee.metrics.test;

import com.codahale.metrics.MetricRegistry;
import com.guicedee.client.IGuiceContext;
import com.guicedee.metrics.MetricsOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MetricsOptions(
        graphite = @MetricsOptions.GraphiteOptions(enabled = true, host = "localhost", port = 2003)
)
public class GraphiteIntegrationTest {

    @Test
    public void testGraphiteConfiguration() {
        IGuiceContext.instance().inject();
        MetricRegistry registry = IGuiceContext.get(MetricRegistry.class);
        Assertions.assertNotNull(registry);
        // If we reach here without exceptions, it means Graphite reporter was at least attempted to be started
        // since it's called in MetricsModule.configure() -> setupGraphite()
    }
}
