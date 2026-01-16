package com.guicedee.metrics.test;

import com.guicedee.metrics.Metrics;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class TestGuicedMetrics implements Metrics {
    @Override
    public Map<Class<? extends Annotation>, Class<? extends MethodInterceptor>> annotations() {
        Map<Class<? extends Annotation>, Class<? extends MethodInterceptor>> map = new HashMap<>();
        map.put(CustomMetric.class, CustomMetricInterceptor.class);
        return map;
    }
}
