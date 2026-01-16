package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Gauge;

public class MPGauge<T extends Number> implements Gauge<T> {
    private final com.codahale.metrics.Gauge<T> delegate;

    public MPGauge(com.codahale.metrics.Gauge<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T getValue() {
        return delegate.getValue();
    }
}
