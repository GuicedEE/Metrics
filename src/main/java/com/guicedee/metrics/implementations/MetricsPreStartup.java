package com.guicedee.metrics.implementations;

import com.guicedee.client.IGuiceContext;
import com.guicedee.client.services.lifecycle.IGuicePreStartup;
import com.guicedee.metrics.MetricsOptions;
import io.vertx.core.Future;
import lombok.Getter;

import java.util.List;

public class MetricsPreStartup implements IGuicePreStartup<MetricsPreStartup> {

    @Getter
    private static MetricsOptions options;

    public static MetricsOptions getOptions() {
        return options;
    }

    @Override
    public List<Future<Boolean>> onStartup() {
        var scanResult = IGuiceContext.instance().getScanResult();
        var classes = scanResult.getClassesWithAnnotation(MetricsOptions.class);
        if (!classes.isEmpty()) {
            // Take the first one found
            var clazz = classes.getFirst().loadClass();
            options = clazz.getAnnotation(MetricsOptions.class);
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
