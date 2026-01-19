package com.guicedee.metrics.test;

import com.guicedee.metrics.MetricMethod;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;

public class TestService {
    public static Lookup getModuleLookup() {
        return MethodHandles.lookup();
    }

    @MetricMethod
    public long metricMethod(String name) {
        return 0;
    }

    @MetricMethod(name = "explicitMetric")
    public long explicitMetricMethod(String suffix) {
        return 0;
    }

    @Counted
    public void countedMethod() {}

    @Timed
    public void timedMethod() {}

    @Counted(name = "meteredMethod")
    public void meteredMethod() {}

    @CustomMetric
    public void customMethod() {}
}
