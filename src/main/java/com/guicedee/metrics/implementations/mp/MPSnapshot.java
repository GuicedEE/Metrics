package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Snapshot;
import java.io.OutputStream;

public class MPSnapshot extends Snapshot {
    private final com.codahale.metrics.Snapshot delegate;

    public MPSnapshot(com.codahale.metrics.Snapshot delegate) {
        this.delegate = delegate;
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public double getMax() {
        return delegate.getMax();
    }

    @Override
    public double getMean() {
        return delegate.getMean();
    }

    @Override
    public PercentileValue[] percentileValues() {
        // Dropwizard doesn't have percentile values in the same way, but we can map standard ones
        return new PercentileValue[] {
            new PercentileValue(0.5, delegate.getMedian()),
            new PercentileValue(0.75, delegate.get75thPercentile()),
            new PercentileValue(0.95, delegate.get95thPercentile()),
            new PercentileValue(0.98, delegate.get98thPercentile()),
            new PercentileValue(0.99, delegate.get99thPercentile()),
            new PercentileValue(0.999, delegate.get999thPercentile())
        };
    }

    @Override
    public HistogramBucket[] bucketValues() {
        return new HistogramBucket[0];
    }

    @Override
    public void dump(OutputStream output) {
        delegate.dump(output);
    }
}
