package com.guicedee.metrics.implementations;

import org.eclipse.microprofile.metrics.annotation.Counted;
import com.guicedee.metrics.implementations.mp.MPCounter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;

public class CountedInterceptor implements MethodInterceptor {

    @Inject
    private Provider<MetricRegistry> registryProvider;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Counted annotation = invocation.getMethod().getAnnotation(Counted.class);
        if (annotation == null) {
            annotation = invocation.getMethod().getDeclaringClass().getAnnotation(Counted.class);
        }
        
        String name = annotation.name();
        if (name.isEmpty()) {
            name = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
        }

        MetricRegistry registry = registryProvider.get();
        if (registry == null) {
            return invocation.proceed();
        }

        Tag[] tags = new Tag[annotation.tags().length];
        for (int i = 0; i < annotation.tags().length; i++) {
            String tag = annotation.tags()[i];
            int eqIndex = tag.indexOf('=');
            if (eqIndex > 0) {
                tags[i] = new Tag(tag.substring(0, eqIndex), tag.substring(eqIndex + 1));
            } else {
                tags[i] = new Tag(tag, "");
            }
        }

        org.eclipse.microprofile.metrics.Counter counter = registry.counter(name, tags);
        counter.inc();
        try {
            return invocation.proceed();
        } finally {
            // In MP Metrics 5.x, @Counted is usually monotonic. 
            // The previous implementation had a "monotonic" flag in custom annotation.
            // MP @Counted doesn't have it.
        }
    }
}
