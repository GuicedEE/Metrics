package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.Snapshot;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * MicroProfile {@link Timer} implementation backed by a Dropwizard {@link com.codahale.metrics.Timer}.
 */
public class MPTimer implements Timer {
    private final com.codahale.metrics.Timer delegate;

    /**
     * Creates an MP timer wrapping the given Dropwizard timer.
     *
     * @param delegate the Dropwizard timer to delegate to
     */
    public MPTimer(com.codahale.metrics.Timer delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Duration duration) {
        delegate.update(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T time(Callable<T> event) throws Exception {
        return delegate.time(event);
    }

    /** {@inheritDoc} */
    @Override
    public void time(Runnable event) {
        delegate.time(event);
    }

    /** {@inheritDoc} */
    @Override
    public Context time() {
        return new MPContext(delegate.time());
    }

    /** {@inheritDoc} */
    @Override
    public Duration getElapsedTime() {
        // Dropwizard doesn't have aggregate elapsed time easily accessible
        return Duration.ZERO;
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

    /**
     * MicroProfile {@link Context} implementation backed by a Dropwizard timer context.
     */
    public static class MPContext implements Context {
        private final com.codahale.metrics.Timer.Context delegate;

        /**
         * Creates an MP context wrapping the given Dropwizard timer context.
         *
         * @param delegate the Dropwizard timer context
         */
        public MPContext(com.codahale.metrics.Timer.Context delegate) {
            this.delegate = delegate;
        }

        /** {@inheritDoc} */
        @Override
        public long stop() {
            return delegate.stop();
        }

        /** {@inheritDoc} */
        @Override
        public void close() {
            delegate.close();
        }
    }
}
