package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.Snapshot;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MPTimer implements Timer {
    private final com.codahale.metrics.Timer delegate;

    public MPTimer(com.codahale.metrics.Timer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void update(Duration duration) {
        delegate.update(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public <T> T time(Callable<T> event) throws Exception {
        return delegate.time(event);
    }

    @Override
    public void time(Runnable event) {
        delegate.time(event);
    }

    @Override
    public Context time() {
        return new MPContext(delegate.time());
    }

    @Override
    public Duration getElapsedTime() {
        // Dropwizard doesn't have aggregate elapsed time easily accessible
        return Duration.ZERO;
    }

    @Override
    public long getCount() {
        return delegate.getCount();
    }

    @Override
    public Snapshot getSnapshot() {
        return new MPSnapshot(delegate.getSnapshot());
    }

    public static class MPContext implements Context {
        private final com.codahale.metrics.Timer.Context delegate;

        public MPContext(com.codahale.metrics.Timer.Context delegate) {
            this.delegate = delegate;
        }

        @Override
        public long stop() {
            return delegate.stop();
        }

        @Override
        public void close() {
            delegate.close();
        }
    }
}
