package com.guicedee.metrics.implementations;

import com.guicedee.client.utils.Discovery;
import com.guicedee.client.services.lifecycle.IGuicePreStartup;
import com.guicedee.metrics.Match;
import com.guicedee.metrics.MetricsOptions;
import io.vertx.core.Future;
import lombok.Getter;

import java.util.List;

/**
 * Pre-startup hook that discovers {@link MetricsOptions} from the classpath
 * and makes the resolved options available to other metrics components.
 */
public class MetricsPreStartup implements IGuicePreStartup<MetricsPreStartup> {

    @Getter
    private static MetricsOptions options;

    /**
     * Scans for {@link MetricsOptions} annotations and initializes the configuration.
     *
     * @return a list of futures representing startup completion
     */
    @Override
    public List<Future<Boolean>> onStartup() {
        var annotations = Discovery.annotations(MetricsOptions.class);
        MetricsOptions annotation = annotations.isEmpty() ? null : annotations.getFirst();
        options = new MetricsOptions() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return MetricsOptions.class;
            }

            @Override
            public boolean enabled() {
                return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_ENABLED", String.valueOf((annotation == null ? true : annotation.enabled()))));
            }

            @Override
            public String registryName() {
                return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_REGISTRY_NAME", (annotation == null ? "vertx" : annotation.registryName()));
            }

            @Override
            public boolean jmxEnabled() {
                return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_JMX_ENABLED", String.valueOf((annotation == null ? true : annotation.jmxEnabled()))));
            }

            @Override
            public String jmxDomain() {
                return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_JMX_DOMAIN", (annotation == null ? "vertx" : annotation.jmxDomain()));
            }

            @Override
            public String baseName() {
                return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_BASE_NAME", (annotation == null ? "vertx" : annotation.baseName()));
            }

            @Override
            public Match[] monitoredEventBusHandlers() {
                return (annotation == null ? new Match[0] : annotation.monitoredEventBusHandlers());
            }

            @Override
            public Match[] monitoredHttpServerUris() {
                return (annotation == null ? new Match[0] : annotation.monitoredHttpServerUris());
            }

            @Override
            public Match[] monitoredHttpServerRoutes() {
                return (annotation == null ? new Match[0] : annotation.monitoredHttpServerRoutes());
            }

            @Override
            public Match[] monitoredHttpClientEndpoints() {
                return (annotation == null ? new Match[0] : annotation.monitoredHttpClientEndpoints());
            }

            @Override
            public GraphiteOptions graphite() {
                GraphiteOptions graphite = (annotation == null ? null : annotation.graphite());
                return new GraphiteOptions() {
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return GraphiteOptions.class;
                    }

                    @Override
                    public boolean enabled() {
                        return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_ENABLED", String.valueOf((graphite == null ? false : graphite.enabled()))));
                    }

                    @Override
                    public String host() {
                        return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_HOST", (graphite == null ? "localhost" : graphite.host()));
                    }

                    @Override
                    public int port() {
                        return Integer.parseInt(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_PORT", String.valueOf((graphite == null ? 2003 : graphite.port()))));
                    }

                    @Override
                    public String prefix() {
                        return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_PREFIX", (graphite == null ? "" : graphite.prefix()));
                    }
                };
            }

            @Override
            public PrometheusOptions prometheus() {
                PrometheusOptions prometheus = (annotation == null ? null : annotation.prometheus());
                return new PrometheusOptions() {
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return PrometheusOptions.class;
                    }

                    @Override
                    public boolean enabled() {
                        return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_PROMETHEUS_ENABLED", String.valueOf((prometheus == null ? false : prometheus.enabled()))));
                    }

                    @Override
                    public String endpoint() {
                        return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_PROMETHEUS_ENDPOINT", (prometheus == null ? "/metrics" : prometheus.endpoint()));
                    }

                    @Override
                    public int port() {
                        return Integer.parseInt(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_PROMETHEUS_PORT", String.valueOf((prometheus == null ? 9090 : prometheus.port()))));
                    }
                };
            }
        };
        return List.of(Future.succeededFuture(true));
    }

    /**
     * Returns the sort order for this pre-startup hook.
     *
     * @return the sort order value
     */
    @Override
    public Integer sortOrder() {
        // Resolve metrics before Hazelcast (+37) and Vert.x (+38) compose runtime options.
        return Integer.MIN_VALUE + 36;
    }
}
