package com.guicedee.metrics;

import java.lang.annotation.*;

/**
 * Marks a method as a metric where the input String is the name of the metric,
 * and the result is the current value of the counter.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MetricMethod {
    /**
     * The name of the metric. If empty, the method name will be used.
     * If the method has a String parameter, it will be used as the metric name suffix.
     *
     * @return The name of the metric.
     */
    String name() default "";

    /**
     * The tags for the metric.
     *
     * @return The tags.
     */
    String[] tags() default {};
}
