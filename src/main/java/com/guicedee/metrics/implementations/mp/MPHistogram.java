package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Snapshot;

public class MPHistogram implements Histogram {
    private final com.codahale.metrics.Histogram delegate;

    public MPHistogram(com.codahale.metrics.Histogram delegate) {
        this.delegate = delegate;
    }

    @Override
    public void update(int value) {
        delegate.update(value);
    }

    @Override
    public void update(long value) {
        delegate.update(value);
    }

    @Override
    public long getCount() {
        return delegate.getCount();
    }

    @Override
    public Snapshot getSnapshot() {
        return new MPSnapshot(delegate.getSnapshot());
    }

    @Override
    public long getSum() {
        return 0;
    }
}
