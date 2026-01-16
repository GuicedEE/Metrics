package com.guicedee.metrics.implementations.mp;

import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Timer;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MPMetricRegistry implements MetricRegistry {
    private final com.codahale.metrics.MetricRegistry delegate;
    private final String scope;

    public MPMetricRegistry(com.codahale.metrics.MetricRegistry delegate, String scope) {
        this.delegate = delegate;
        this.scope = scope;
    }

    private String getQualifiedName(String name, Tag... tags) {
        if (tags == null || tags.length == 0) {
            return name;
        }
        StringBuilder sb = new StringBuilder(name);
        for (Tag tag : tags) {
            if (tag != null) {
                sb.append(";").append(tag.getTagName()).append("=").append(tag.getTagValue());
            }
        }
        return sb.toString();
    }

    private String getQualifiedName(MetricID metricID) {
        String name = metricID.getName();
        Map<String, String> tags = metricID.getTags();
        if (tags == null || tags.isEmpty()) {
            return name;
        }
        StringBuilder sb = new StringBuilder(name);
        tags.forEach((k, v) -> sb.append(";").append(k).append("=").append(v));
        return sb.toString();
    }

    @Override
    public Counter counter(String name) {
        return new MPCounter(delegate.counter(name));
    }

    @Override
    public Counter counter(String name, Tag... tags) {
        return new MPCounter(delegate.counter(getQualifiedName(name, tags)));
    }

    @Override
    public Counter counter(MetricID metricID) {
        return new MPCounter(delegate.counter(getQualifiedName(metricID)));
    }

    @Override
    public Counter counter(Metadata metadata) {
        return counter(metadata.getName());
    }

    @Override
    public Counter counter(Metadata metadata, Tag... tags) {
        return counter(metadata.getName(), tags);
    }

    @Override
    public <T, R extends Number> Gauge<R> gauge(String name, T object, Function<T, R> func, Tag... tags) {
        com.codahale.metrics.Gauge<R> dwGauge = () -> func.apply(object);
        delegate.register(getQualifiedName(name, tags), dwGauge);
        return new MPGauge<>(dwGauge);
    }

    @Override
    public <T, R extends Number> Gauge<R> gauge(MetricID metricID, T object, Function<T, R> func) {
        com.codahale.metrics.Gauge<R> dwGauge = () -> func.apply(object);
        delegate.register(getQualifiedName(metricID), dwGauge);
        return new MPGauge<>(dwGauge);
    }

    @Override
    public <T, R extends Number> Gauge<R> gauge(Metadata metadata, T object, Function<T, R> func, Tag... tags) {
        return gauge(metadata.getName(), object, func, tags);
    }

    @Override
    public <T extends Number> Gauge<T> gauge(String name, Supplier<T> supplier, Tag... tags) {
        com.codahale.metrics.Gauge<T> dwGauge = supplier::get;
        delegate.register(getQualifiedName(name, tags), dwGauge);
        return new MPGauge<>(dwGauge);
    }

    @Override
    public <T extends Number> Gauge<T> gauge(MetricID metricID, Supplier<T> supplier) {
        com.codahale.metrics.Gauge<T> dwGauge = supplier::get;
        delegate.register(getQualifiedName(metricID), dwGauge);
        return new MPGauge<>(dwGauge);
    }

    @Override
    public <T extends Number> Gauge<T> gauge(Metadata metadata, Supplier<T> supplier, Tag... tags) {
        return gauge(metadata.getName(), supplier, tags);
    }

    @Override
    public Histogram histogram(String name) {
        return new MPHistogram(delegate.histogram(name));
    }

    @Override
    public Histogram histogram(String name, Tag... tags) {
        return new MPHistogram(delegate.histogram(getQualifiedName(name, tags)));
    }

    @Override
    public Histogram histogram(MetricID metricID) {
        return new MPHistogram(delegate.histogram(getQualifiedName(metricID)));
    }

    @Override
    public Histogram histogram(Metadata metadata) {
        return histogram(metadata.getName());
    }

    @Override
    public Histogram histogram(Metadata metadata, Tag... tags) {
        return histogram(metadata.getName(), tags);
    }

    @Override
    public Timer timer(String name) {
        return new MPTimer(delegate.timer(name));
    }

    @Override
    public Timer timer(String name, Tag... tags) {
        return new MPTimer(delegate.timer(getQualifiedName(name, tags)));
    }

    @Override
    public Timer timer(MetricID metricID) {
        return new MPTimer(delegate.timer(getQualifiedName(metricID)));
    }

    @Override
    public Timer timer(Metadata metadata) {
        return timer(metadata.getName());
    }

    @Override
    public Timer timer(Metadata metadata, Tag... tags) {
        return timer(metadata.getName(), tags);
    }

    @Override
    public Metric getMetric(MetricID metricID) {
        com.codahale.metrics.Metric metric = delegate.getMetrics().get(getQualifiedName(metricID));
        return wrap(metric);
    }

    private Metric wrap(com.codahale.metrics.Metric metric) {
        if (metric instanceof com.codahale.metrics.Counter) return new MPCounter((com.codahale.metrics.Counter) metric);
        if (metric instanceof com.codahale.metrics.Histogram) return new MPHistogram((com.codahale.metrics.Histogram) metric);
        if (metric instanceof com.codahale.metrics.Timer) return new MPTimer((com.codahale.metrics.Timer) metric);
        if (metric instanceof com.codahale.metrics.Gauge) return new MPGauge<>((com.codahale.metrics.Gauge<? extends Number>) metric);
        return null;
    }

    @Override
    public <T extends Metric> T getMetric(MetricID metricID, Class<T> clazz) {
        return clazz.cast(getMetric(metricID));
    }

    @Override
    public Counter getCounter(MetricID metricID) {
        return (Counter) getMetric(metricID);
    }

    @Override
    public Gauge<?> getGauge(MetricID metricID) {
        return (Gauge<?>) getMetric(metricID);
    }

    @Override
    public Histogram getHistogram(MetricID metricID) {
        return (Histogram) getMetric(metricID);
    }

    @Override
    public Timer getTimer(MetricID metricID) {
        return (Timer) getMetric(metricID);
    }

    @Override
    public Metadata getMetadata(String name) {
        return new MetadataBuilder().withName(name).build();
    }

    @Override
    public boolean remove(String name) {
        return delegate.remove(name);
    }

    @Override
    public boolean remove(MetricID metricID) {
        return delegate.remove(getQualifiedName(metricID));
    }

    @Override
    public void removeMatching(MetricFilter filter) {
        delegate.removeMatching((name, metric) -> filter.matches(new MetricID(name), wrap(metric)));
    }

    @Override
    public SortedSet<String> getNames() {
        return new TreeSet<>(delegate.getNames());
    }

    @Override
    public SortedSet<MetricID> getMetricIDs() {
        return delegate.getNames().stream().map(MetricID::new).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public SortedMap<MetricID, Gauge> getGauges() {
        return getGauges(MetricFilter.ALL);
    }

    @Override
    public SortedMap<MetricID, Gauge> getGauges(MetricFilter filter) {
        SortedMap<MetricID, Gauge> map = new TreeMap<>();
        delegate.getGauges().forEach((name, dwGauge) -> {
            MetricID id = new MetricID(name);
            MPGauge gauge = new MPGauge(dwGauge);
            if (filter.matches(id, gauge)) {
                map.put(id, gauge);
            }
        });
        return map;
    }

    @Override
    public SortedMap<MetricID, Counter> getCounters() {
        return getCounters(MetricFilter.ALL);
    }

    @Override
    public SortedMap<MetricID, Counter> getCounters(MetricFilter filter) {
        SortedMap<MetricID, Counter> map = new TreeMap<>();
        delegate.getCounters().forEach((name, dwCounter) -> {
            MetricID id = new MetricID(name);
            MPCounter counter = new MPCounter(dwCounter);
            if (filter.matches(id, counter)) {
                map.put(id, counter);
            }
        });
        return map;
    }

    @Override
    public SortedMap<MetricID, Histogram> getHistograms() {
        return getHistograms(MetricFilter.ALL);
    }

    @Override
    public SortedMap<MetricID, Histogram> getHistograms(MetricFilter filter) {
        SortedMap<MetricID, Histogram> map = new TreeMap<>();
        delegate.getHistograms().forEach((name, dwHistogram) -> {
            MetricID id = new MetricID(name);
            MPHistogram histogram = new MPHistogram(dwHistogram);
            if (filter.matches(id, histogram)) {
                map.put(id, histogram);
            }
        });
        return map;
    }

    @Override
    public SortedMap<MetricID, Timer> getTimers() {
        return getTimers(MetricFilter.ALL);
    }

    @Override
    public SortedMap<MetricID, Timer> getTimers(MetricFilter filter) {
        SortedMap<MetricID, Timer> map = new TreeMap<>();
        delegate.getTimers().forEach((name, dwTimer) -> {
            MetricID id = new MetricID(name);
            MPTimer timer = new MPTimer(dwTimer);
            if (filter.matches(id, timer)) {
                map.put(id, timer);
            }
        });
        return map;
    }

    @Override
    public SortedMap<MetricID, Metric> getMetrics(MetricFilter filter) {
        SortedMap<MetricID, Metric> map = new TreeMap<>();
        delegate.getMetrics().forEach((name, dwMetric) -> {
            MetricID id = new MetricID(name);
            Metric mpMetric = wrap(dwMetric);
            if (filter.matches(id, mpMetric)) {
                map.put(id, mpMetric);
            }
        });
        return map;
    }

    @Override
    public <T extends Metric> SortedMap<MetricID, T> getMetrics(Class<T> clazz, MetricFilter filter) {
        SortedMap<MetricID, T> map = new TreeMap<>();
        getMetrics(filter).forEach((id, metric) -> {
            if (clazz.isInstance(metric)) {
                map.put(id, clazz.cast(metric));
            }
        });
        return map;
    }

    @Override
    public Map<MetricID, Metric> getMetrics() {
        return getMetrics(MetricFilter.ALL);
    }

    @Override
    public Map<String, Metadata> getMetadata() {
        Map<String, Metadata> map = new HashMap<>();
        delegate.getNames().forEach(name -> map.put(name, getMetadata(name)));
        return map;
    }

    @Override
    public String getScope() {
        return scope;
    }
}
