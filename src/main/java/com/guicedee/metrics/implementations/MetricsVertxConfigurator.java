package com.guicedee.metrics.implementations;

import com.guicedee.metrics.Match;
import com.guicedee.metrics.MetricsOptions;
import com.guicedee.vertx.spi.VertxConfigurator;
import io.vertx.core.VertxBuilder;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

import io.vertx.core.VertxOptions;

public class MetricsVertxConfigurator implements VertxConfigurator {
    @Override
    public VertxBuilder builder(VertxBuilder builder) {
        MetricsOptions options = MetricsPreStartup.getOptions();
        DropwizardMetricsOptions dwOptions = new DropwizardMetricsOptions();
        if (options != null) {
            dwOptions.setEnabled(options.enabled())
                    .setRegistryName(options.registryName())
                    .setJmxEnabled(options.jmxEnabled())
                    .setJmxDomain(options.jmxDomain())
                    .setBaseName(options.baseName());

            for (Match match : options.monitoredEventBusHandlers()) {
                dwOptions.addMonitoredEventBusHandler(toMatch(match));
            }
            for (Match match : options.monitoredHttpServerUris()) {
                dwOptions.addMonitoredHttpServerUri(toMatch(match));
            }
            for (Match match : options.monitoredHttpServerRoutes()) {
                dwOptions.addMonitoredHttpServerRoute(toMatch(match));
            }
            for (Match match : options.monitoredHttpClientEndpoints()) {
                dwOptions.addMonitoredHttpClientEndpoint(toMatch(match));
            }
        } else {
            dwOptions.setEnabled(true)
                    .setRegistryName("vertx")
                    .setJmxEnabled(true);
        }
        return builder.with(new VertxOptions().setMetricsOptions(dwOptions));
    }

    private io.vertx.ext.dropwizard.Match toMatch(Match match) {
        io.vertx.ext.dropwizard.Match m = new io.vertx.ext.dropwizard.Match();
        m.setValue(match.value());
        m.setType(match.type() == Match.MatchType.REGEX ? io.vertx.ext.dropwizard.MatchType.REGEX : io.vertx.ext.dropwizard.MatchType.EQUALS);
        if (match.alias() != null && !match.alias().isEmpty()) {
            m.setAlias(match.alias());
        }
        return m;
    }
}
