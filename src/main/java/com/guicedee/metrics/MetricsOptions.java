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
     *
     * @return {@code true} if metrics are enabled
     */
    boolean enabled() default true;

    /**
     * The name of the registry to use.
     *
     * @return the registry name
     */
    String registryName() default "vertx";

    /**
     * Whether JMX is enabled.
     *
     * @return {@code true} if JMX reporting is enabled
     */
    boolean jmxEnabled() default true;

    /**
     * The JMX domain to use.
     *
     * @return the JMX domain
     */
    String jmxDomain() default "vertx";

    /**
     * The base name for metrics.
     *
     * @return the base metric name
     */
    String baseName() default "vertx";

    /**
     * Monitored event bus handlers.
     *
     * @return the match configurations for event bus handlers
     */
    Match[] monitoredEventBusHandlers() default {};

    /**
     * Monitored HTTP server URIs.
     *
     * @return the match configurations for HTTP server URIs
     */
    Match[] monitoredHttpServerUris() default {};

    /**
     * Monitored HTTP server routes.
     *
     * @return the match configurations for HTTP server routes
     */
    Match[] monitoredHttpServerRoutes() default {};

    /**
     * Monitored HTTP client endpoints.
     *
     * @return the match configurations for HTTP client endpoints
     */
    Match[] monitoredHttpClientEndpoints() default {};

    /**
     * Graphite configuration.
     *
     * @return the Graphite options
     */
    GraphiteOptions graphite() default @GraphiteOptions(enabled = false);

    /**
     * Prometheus configuration.
     *
     * @return the Prometheus options
     */
    PrometheusOptions prometheus() default @PrometheusOptions(enabled = false);

    /**
     * Configuration for Graphite metric reporting.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface GraphiteOptions {
        /**
         * Whether Graphite reporting is enabled.
         *
         * @return {@code true} if enabled
         */
        boolean enabled() default false;

        /**
         * The Graphite host.
         *
         * @return the host name
         */
        String host() default "localhost";

        /**
         * The Graphite port.
         *
         * @return the port number
         */
        int port() default 2003;

        /**
         * The metric name prefix.
         *
         * @return the prefix
         */
        String prefix() default "";
    }

    /**
     * Configuration for Prometheus metric reporting.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface PrometheusOptions {
        /**
         * Whether Prometheus reporting is enabled.
         *
         * @return {@code true} if enabled
         */
        boolean enabled() default false;

        /**
         * The endpoint path to expose metrics.
         *
         * @return the endpoint path
         */
        String endpoint() default "/metrics";

        /**
         * The port for the Prometheus endpoint.
         *
         * @return the port number
         */
        int port() default 9090;
    }
}
