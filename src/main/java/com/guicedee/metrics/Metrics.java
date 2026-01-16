package com.guicedee.metrics;

import org.aopalliance.intercept.MethodInterceptor;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Interface to define possible annotations and their interceptors for the metrics module.
 * Implementations of this interface can be used to register custom metric annotations.
 */
public interface Metrics {
    /**
     * Returns a map of annotations and their corresponding interceptors to be bound in Guice.
     *
     * @return a map where the key is the annotation class and the value is the interceptor class.
     */
    Map<Class<? extends Annotation>, Class<? extends MethodInterceptor>> annotations();
}
