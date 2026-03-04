package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Gauge;

/**
 * MicroProfile {@link Gauge} implementation backed by a Dropwizard gauge.
 *
 * @param <T> the numeric value type
 */
public class MPGauge<T extends Number> implements Gauge<T> {
    private final com.codahale.metrics.Gauge<T> delegate;

    /**
     * Creates an MP gauge wrapping the given Dropwizard gauge.
     *
     * @param delegate the Dropwizard gauge to delegate to
     */
    public MPGauge(com.codahale.metrics.Gauge<T> delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public T getValue() {
        return delegate.getValue();
    }
}
