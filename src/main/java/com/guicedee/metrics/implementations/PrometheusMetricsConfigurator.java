package com.guicedee.metrics.implementations;

import com.guicedee.metrics.MetricsOptions;
import com.guicedee.vertx.web.spi.VertxRouterConfigurator;
import io.vertx.ext.web.Router;

/**
 * Configures the Vert.x router with a Prometheus metrics endpoint when enabled.
 */
public class PrometheusMetricsConfigurator implements VertxRouterConfigurator<PrometheusMetricsConfigurator>, com.guicedee.vertx.web.spi.ManagedHttpListenerProvider {
    /**
     * Registers the Prometheus metrics route if Prometheus is enabled.
     *
     * @param builder the router to configure
     * @return the configured router
     */
    @Override
    public Router builder(Router builder) {
        MetricsOptions options = MetricsPreStartup.getOptions();
        if (options != null && options.enabled() && options.prometheus().enabled() && options.prometheus().port() == -1) {
            builder.get(options.prometheus().endpoint())
                    .handler(new PrometheusMetricsHandler());
        }
        return builder;
    }

    @Override
    public java.util.List<com.guicedee.vertx.web.spi.ManagedHttpListener> listeners() {
        MetricsOptions options = MetricsPreStartup.getOptions();
        if (options == null || !options.enabled() || !options.prometheus().enabled()) return java.util.List.of();
        int port = options.prometheus().port();
        if (port < -1 || port > 65535) throw new IllegalArgumentException("METRICS_PROMETHEUS_PORT must be -1 or 0..65535");
        if (port == -1) return java.util.List.of();
        return java.util.List.of(new com.guicedee.vertx.web.spi.ManagedHttpListener("prometheus",
                new io.vertx.core.http.HttpServerOptions().setPort(port), vertx -> {
                    Router router = Router.router(vertx);
                    router.get(options.prometheus().endpoint()).handler(new PrometheusMetricsHandler());
                    return router;
                }));
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
