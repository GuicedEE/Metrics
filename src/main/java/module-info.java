import com.guicedee.metrics.Metrics;

module com.guicedee.metrics {
    requires static lombok;

    requires transitive com.guicedee.client;
    requires transitive com.guicedee.guicedinjection;
    requires transitive com.guicedee.vertx;
    requires transitive io.vertx.metrics.dropwizard;
    // Graphite is in the same module as metrics-core in this project's shaded version
    requires transitive com.codahale.metrics;
    requires static com.guicedee.vertx.web;

    requires io.vertx.core;
    requires io.vertx.core.logging;

    exports com.guicedee.metrics;
    exports com.guicedee.metrics.implementations;
    exports com.guicedee.metrics.enumerations;
    exports com.guicedee.metrics.implementations.mp;

    opens com.guicedee.metrics to com.google.guice, com.fasterxml.jackson.databind, com.guicedee.guicedinjection, com.guicedee.client, guiced.metrics.test, com.google.common;
    opens com.guicedee.metrics.implementations to com.google.guice, com.fasterxml.jackson.databind, com.guicedee.guicedinjection, com.guicedee.client, guiced.metrics.test, com.google.common;
    opens com.guicedee.metrics.implementations.mp to com.google.guice, com.fasterxml.jackson.databind, com.guicedee.guicedinjection, com.guicedee.client, guiced.metrics.test, com.google.common;

    provides com.guicedee.client.services.lifecycle.IGuiceModule with com.guicedee.metrics.implementations.MetricsModule;
    provides com.guicedee.client.services.lifecycle.IGuicePreStartup with com.guicedee.metrics.implementations.MetricsPreStartup;
    provides com.guicedee.vertx.spi.VertxConfigurator with com.guicedee.metrics.implementations.MetricsVertxConfigurator;
    provides com.guicedee.vertx.web.spi.VertxRouterConfigurator with com.guicedee.metrics.implementations.PrometheusMetricsConfigurator;

    uses Metrics;
}
