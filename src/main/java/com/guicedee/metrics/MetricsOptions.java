package com.guicedee.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure Dropwizard metrics for Vert.x.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface MetricsOptions {
    /**
     * Whether metrics are enabled.
     */
    boolean enabled() default true;

    /**
     * The name of the registry to use.
     */
    String registryName() default "vertx";

    /**
     * Whether JMX is enabled.
     */
    boolean jmxEnabled() default true;

    /**
     * The JMX domain to use.
     */
    String jmxDomain() default "vertx";

    /**
     * The base name for metrics.
     */
    String baseName() default "vertx";

    /**
     * Monitored event bus handlers.
     */
    Match[] monitoredEventBusHandlers() default {};

    /**
     * Monitored HTTP server URIs.
     */
    Match[] monitoredHttpServerUris() default {};

    /**
     * Monitored HTTP server routes.
     */
    Match[] monitoredHttpServerRoutes() default {};

    /**
     * Monitored HTTP client endpoints.
     */
    Match[] monitoredHttpClientEndpoints() default {};

    /**
     * Graphite configuration.
     */
    GraphiteOptions graphite() default @GraphiteOptions(enabled = false);

    /**
     * Prometheus configuration.
     */
    PrometheusOptions prometheus() default @PrometheusOptions(enabled = false);

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface GraphiteOptions {
        boolean enabled() default false;
        String host() default "localhost";
        int port() default 2003;
        String prefix() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface PrometheusOptions {
        boolean enabled() default false;
        String endpoint() default "/metrics";
        int port() default 9090;
    }
}
