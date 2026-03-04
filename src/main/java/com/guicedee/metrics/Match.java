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
     *
     * @return the match value
     */
    String value();

    /**
     * The type of match (EQUALS or REGEX).
     *
     * @return the match type
     */
    MatchType type() default MatchType.EQUALS;

    /**
     * Optional alias for the match.
     *
     * @return the alias, or empty string if none
     */
    String alias() default "";

    /**
     * The type of matching to perform.
     */
    enum MatchType {
        /**
         * Match using exact equality.
         */
        EQUALS,

        /**
         * Match using regular expression.
         */
        REGEX
    }
}
