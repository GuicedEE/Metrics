import com.guicedee.client.services.lifecycle.IGuiceModule;
import com.guicedee.metrics.Metrics;
import com.guicedee.metrics.test.MetricsTestModule;

module guiced.metrics.test {
    requires transitive com.guicedee.metrics;
    requires org.junit.jupiter.api;
    requires static lombok;
    requires io.vertx.core;
    requires io.vertx.metrics.dropwizard;
    requires com.guicedee.vertx.web;

    exports com.guicedee.metrics.test;
    opens com.guicedee.metrics.test to org.junit.platform.commons,com.google.guice, com.google.common,com.guicedee.guicedinjection;

    provides IGuiceModule with MetricsTestModule;
    provides Metrics with com.guicedee.metrics.test.TestGuicedMetrics;

    uses com.guicedee.vertx.web.spi.VertxRouterConfigurator;
}