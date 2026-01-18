package com.guicedee.metrics.implementations;

import com.guicedee.metrics.MetricsOptions;
import com.guicedee.vertx.web.spi.VertxRouterConfigurator;
import io.vertx.ext.web.Router;

public class PrometheusMetricsConfigurator implements VertxRouterConfigurator<PrometheusMetricsConfigurator> {
    @Override
    public Router builder(Router builder) {
        MetricsOptions options = MetricsPreStartup.getOptions();
        if (options != null && options.prometheus().enabled()) {
            builder.get(options.prometheus().endpoint())
                    .handler(new PrometheusMetricsHandler());
        }
        return builder;
    }

    @Override
    public Integer sortOrder() {
        return Integer.MIN_VALUE + 70;
    }
}
