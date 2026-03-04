package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Counter;

/**
 * MicroProfile {@link Counter} implementation backed by a Dropwizard counter.
 */
public class MPCounter implements Counter {
    private final com.codahale.metrics.Counter delegate;

    /**
     * Creates an MP counter wrapping the given Dropwizard counter.
     *
     * @param delegate the Dropwizard counter to delegate to
     */
    public MPCounter(com.codahale.metrics.Counter delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void inc() {
        delegate.inc();
    }

    /** {@inheritDoc} */
    @Override
    public void inc(long n) {
        delegate.inc(n);
    }

    /** {@inheritDoc} */
    @Override
    public long getCount() {
        return delegate.getCount();
    }

    /**
     * Decrements the counter by one.
     */
    public void dec() {
        delegate.dec();
    }

    /**
     * Decrements the counter by the given amount.
     *
     * @param n the amount to decrement
     */
    public void dec(long n) {
        delegate.dec(n);
    }
}
