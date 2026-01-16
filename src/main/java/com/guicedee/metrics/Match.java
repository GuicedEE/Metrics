package com.guicedee.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to represent a metric match configuration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Match {
    /**
     * The value to match (address, URI, etc.).
     */
    String value();

    /**
     * The type of match (EQUALS or REGEX).
     */
    MatchType type() default MatchType.EQUALS;

    /**
     * Optional alias for the match.
     */
    String alias() default "";

    enum MatchType {
        EQUALS,
        REGEX
    }
}
