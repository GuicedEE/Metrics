package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Snapshot;

/**
 * MicroProfile {@link Histogram} implementation backed by a Dropwizard histogram.
 */
public class MPHistogram implements Histogram {
    private final com.codahale.metrics.Histogram delegate;

    /**
     * Creates an MP histogram wrapping the given Dropwizard histogram.
     *
     * @param delegate the Dropwizard histogram to delegate to
     */
    public MPHistogram(com.codahale.metrics.Histogram delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void update(int value) {
        delegate.update(value);
    }

    /** {@inheritDoc} */
    @Override
    public void update(long value) {
        delegate.update(value);
    }

    /** {@inheritDoc} */
    @Override
    public long getCount() {
        return delegate.getCount();
    }

    /** {@inheritDoc} */
    @Override
    public Snapshot getSnapshot() {
        return new MPSnapshot(delegate.getSnapshot());
    }

    /** {@inheritDoc} */
    @Override
    public long getSum() {
        return 0;
    }
}
