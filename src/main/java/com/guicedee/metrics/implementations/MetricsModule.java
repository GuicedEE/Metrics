package com.guicedee.metrics.implementations;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.guicedee.metrics.MetricsOptions;
import com.google.inject.AbstractModule;

import com.guicedee.client.services.lifecycle.IGuiceModule;
import com.google.inject.matcher.Matchers;
import com.guicedee.metrics.*;
import com.guicedee.metrics.implementations.mp.MPMetricRegistryProvider;
import org.aopalliance.intercept.MethodInterceptor;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

public class MetricsModule extends AbstractModule implements IGuiceModule<MetricsModule> {

    @Override
    protected void configure() {
        MetricRegistry registry = getMetricRegistry();
        if (registry != null) {
            bind(com.codahale.metrics.MetricRegistry.class).toInstance(registry);
            setupGraphite(registry);
        }

        bind(org.eclipse.microprofile.metrics.MetricRegistry.class).toProvider(MPMetricRegistryProvider.class).in(com.google.inject.Singleton.class);

        CountedInterceptor countedInterceptor = new CountedInterceptor();
        requestInjection(countedInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(org.eclipse.microprofile.metrics.annotation.Counted.class), countedInterceptor);
        bindInterceptor(Matchers.annotatedWith(org.eclipse.microprofile.metrics.annotation.Counted.class), Matchers.any(), countedInterceptor);

        TimedInterceptor timedInterceptor = new TimedInterceptor();
        requestInjection(timedInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(org.eclipse.microprofile.metrics.annotation.Timed.class), timedInterceptor);
        bindInterceptor(Matchers.annotatedWith(org.eclipse.microprofile.metrics.annotation.Timed.class), Matchers.any(), timedInterceptor);

        ServiceLoader<Metrics> metricsLoaders = ServiceLoader.load(Metrics.class);
        for (Metrics loader : metricsLoaders) {
            Map<Class<? extends java.lang.annotation.Annotation>, Class<? extends MethodInterceptor>> annotations = loader.annotations();
            if (annotations != null) {
                for (Map.Entry<Class<? extends java.lang.annotation.Annotation>, Class<? extends MethodInterceptor>> entry : annotations.entrySet()) {
                    try {
                        MethodInterceptor interceptor = entry.getValue().getDeclaredConstructor().newInstance();
                        requestInjection(interceptor);
                        bindInterceptor(Matchers.any(), Matchers.annotatedWith(entry.getKey()), interceptor);
                    } catch (NoSuchMethodException e) {
                        // Attempt to get from Guice context if it has no default constructor
                        bindInterceptor(Matchers.any(), Matchers.annotatedWith(entry.getKey()), new MethodInterceptor() {
                            @Override
                            public Object invoke(org.aopalliance.intercept.MethodInvocation invocation) throws Throwable {
                                return com.guicedee.client.IGuiceContext.get(entry.getValue()).invoke(invocation);
                            }
                        });
                    } catch (Exception e) {
                        // Log error
                    }
                }
            }
        }
    }

    private void setupGraphite(MetricRegistry registry) {
        MetricsOptions options = MetricsPreStartup.getOptions();
        if (options != null && options.graphite().enabled()) {
            MetricsOptions.GraphiteOptions graphiteOptions = options.graphite();
            final Graphite graphite = new Graphite(new InetSocketAddress(graphiteOptions.host(), graphiteOptions.port()));
            final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                    .prefixedWith(graphiteOptions.prefix())
                    .build(graphite);
            reporter.start(1, TimeUnit.MINUTES);
        }
    }

    private MetricRegistry getMetricRegistry() {
        try {
            MetricsOptions options = MetricsPreStartup.getOptions();
            String registryName = options != null ? options.registryName() : "vertx";
            return SharedMetricRegistries.getOrCreate(registryName);
        } catch (Throwable ignored) {
            // Log if needed
        }
        return null;
    }
}
