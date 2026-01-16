package com.guicedee.metrics.implementations.mp;

import com.codahale.metrics.SharedMetricRegistries;
import com.guicedee.metrics.MetricsOptions;
import com.guicedee.metrics.implementations.MetricsPreStartup;
import com.google.inject.Singleton;
import org.eclipse.microprofile.metrics.MetricRegistry;
import com.google.inject.Provider;

@Singleton
public class MPMetricRegistryProvider implements Provider<MetricRegistry> {

    @Override
    public MetricRegistry get() {
        MetricsOptions options = MetricsPreStartup.getOptions();
        String registryName = (options != null && options.registryName() != null && !options.registryName().isEmpty()) ? options.registryName() : "vertx";
        return new MPMetricRegistry(SharedMetricRegistries.getOrCreate(registryName), MetricRegistry.APPLICATION_SCOPE);
    }
}
