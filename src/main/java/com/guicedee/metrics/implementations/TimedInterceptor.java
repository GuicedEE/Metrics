package com.guicedee.metrics.implementations;

import org.eclipse.microprofile.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.Timer;

public class TimedInterceptor implements MethodInterceptor {

    @Inject
    private Provider<MetricRegistry> registryProvider;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Timed annotation = invocation.getMethod().getAnnotation(Timed.class);
        if (annotation == null) {
            annotation = invocation.getMethod().getDeclaringClass().getAnnotation(Timed.class);
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

        Timer timer = registry.timer(name, tags);
        Timer.Context context = timer.time();
        try {
            return invocation.proceed();
        } finally {
            context.stop();
        }
    }
}
