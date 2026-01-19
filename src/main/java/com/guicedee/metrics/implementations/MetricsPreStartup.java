package com.guicedee.metrics.implementations;

import com.guicedee.client.IGuiceContext;
import com.guicedee.client.services.lifecycle.IGuicePreStartup;
import com.guicedee.metrics.Match;
import com.guicedee.metrics.MetricsOptions;
import io.vertx.core.Future;
import lombok.Getter;

import java.util.List;

public class MetricsPreStartup implements IGuicePreStartup<MetricsPreStartup> {

    @Getter
    private static MetricsOptions options;

    @Override
    public List<Future<Boolean>> onStartup() {
        var scanResult = IGuiceContext.instance().getScanResult();
        var classes = scanResult.getClassesWithAnnotation(MetricsOptions.class);
        if (!classes.isEmpty()) {
            // Take the first one found
            var clazz = classes.getFirst().loadClass();
            MetricsOptions annotation = clazz.getAnnotation(MetricsOptions.class);
            options = new MetricsOptions() {
                @Override
                public Class<? extends java.lang.annotation.Annotation> annotationType() {
                    return MetricsOptions.class;
                }

                @Override
                public boolean enabled() {
                    return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_ENABLED", String.valueOf(annotation.enabled())));
                }

                @Override
                public String registryName() {
                    return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_REGISTRY_NAME", annotation.registryName());
                }

                @Override
                public boolean jmxEnabled() {
                    return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_JMX_ENABLED", String.valueOf(annotation.jmxEnabled())));
                }

                @Override
                public String jmxDomain() {
                    return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_JMX_DOMAIN", annotation.jmxDomain());
                }

                @Override
                public String baseName() {
                    return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_BASE_NAME", annotation.baseName());
                }

                @Override
                public Match[] monitoredEventBusHandlers() {
                    return annotation.monitoredEventBusHandlers();
                }

                @Override
                public Match[] monitoredHttpServerUris() {
                    return annotation.monitoredHttpServerUris();
                }

                @Override
                public Match[] monitoredHttpServerRoutes() {
                    return annotation.monitoredHttpServerRoutes();
                }

                @Override
                public Match[] monitoredHttpClientEndpoints() {
                    return annotation.monitoredHttpClientEndpoints();
                }

                @Override
                public GraphiteOptions graphite() {
                    GraphiteOptions graphite = annotation.graphite();
                    return new GraphiteOptions() {
                        @Override
                        public Class<? extends java.lang.annotation.Annotation> annotationType() {
                            return GraphiteOptions.class;
                        }

                        @Override
                        public boolean enabled() {
                            return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_ENABLED", String.valueOf(graphite.enabled())));
                        }

                        @Override
                        public String host() {
                            return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_HOST", graphite.host());
                        }

                        @Override
                        public int port() {
                            return Integer.parseInt(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_PORT", String.valueOf(graphite.port())));
                        }

                        @Override
                        public String prefix() {
                            return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_GRAPHITE_PREFIX", graphite.prefix());
                        }
                    };
                }

                @Override
                public PrometheusOptions prometheus() {
                    PrometheusOptions prometheus = annotation.prometheus();
                    return new PrometheusOptions() {
                        @Override
                        public Class<? extends java.lang.annotation.Annotation> annotationType() {
                            return PrometheusOptions.class;
                        }

                        @Override
                        public boolean enabled() {
                            return Boolean.parseBoolean(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_PROMETHEUS_ENABLED", String.valueOf(prometheus.enabled())));
                        }

                        @Override
                        public String endpoint() {
                            return com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_PROMETHEUS_ENDPOINT", prometheus.endpoint());
                        }

                        @Override
                        public int port() {
                            return Integer.parseInt(com.guicedee.client.Environment.getSystemPropertyOrEnvironment("METRICS_PROMETHEUS_PORT", String.valueOf(prometheus.port())));
                        }
                    };
                }
            };
        }
        return List.of(Future.succeededFuture(true));
    }

    @Override
    public Integer sortOrder() {
        // Run before VertXPreStartup if possible, or at least before it builds Vertx
        // VertXPreStartup has sortOrder Integer.MIN_VALUE + 50;
        return Integer.MIN_VALUE + 40;
    }
}
