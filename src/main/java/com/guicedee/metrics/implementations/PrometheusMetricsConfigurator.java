package com.guicedee.metrics.implementations;

import com.guicedee.metrics.MetricsOptions;
import com.guicedee.vertx.web.spi.VertxRouterConfigurator;
import io.vertx.ext.web.Router;

/**
 * Configures the Vert.x router with a Prometheus metrics endpoint when enabled.
 */
public class PrometheusMetricsConfigurator implements VertxRouterConfigurator<PrometheusMetricsConfigurator> {
    /**
     * Registers the Prometheus metrics route if Prometheus is enabled.
     *
     * @param builder the router to configure
     * @return the configured router
     */
    @Override
    public Router builder(Router builder) {
        MetricsOptions options = MetricsPreStartup.getOptions();
        if (options != null && options.prometheus().enabled()) {
            builder.get(options.prometheus().endpoint())
                    .handler(new PrometheusMetricsHandler());
        }
        return builder;
    }

    /**
     * Returns the sort order for this configurator.
     *
     * @return the sort order value
     */
    @Override
    public Integer sortOrder() {
        return Integer.MIN_VALUE + 70;
    }
}
