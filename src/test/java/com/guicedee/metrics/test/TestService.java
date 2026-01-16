package com.guicedee.metrics.test;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;

public class TestService {
    public static Lookup getModuleLookup() {
        return MethodHandles.lookup();
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
