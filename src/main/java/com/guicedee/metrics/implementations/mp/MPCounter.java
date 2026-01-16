package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Counter;

public class MPCounter implements Counter {
    private final com.codahale.metrics.Counter delegate;

    public MPCounter(com.codahale.metrics.Counter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void inc() {
        delegate.inc();
    }

    @Override
    public void inc(long n) {
        delegate.inc(n);
    }

    @Override
    public long getCount() {
        return delegate.getCount();
    }

    public void dec() {
        delegate.dec();
    }

    public void dec(long n) {
        delegate.dec(n);
    }
}
