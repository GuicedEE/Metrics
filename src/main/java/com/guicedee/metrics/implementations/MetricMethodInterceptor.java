package com.guicedee.metrics.implementations;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.guicedee.metrics.MetricMethod;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;

import java.lang.reflect.Method;

public class MetricMethodInterceptor implements MethodInterceptor {

    @Inject
    private Provider<MetricRegistry> registryProvider;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        MetricMethod annotation = method.getAnnotation(MetricMethod.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(MetricMethod.class);
        }

        String name = annotation.name();
        Object[] arguments = invocation.getArguments();
        
        // If the method has a String parameter, use it as the name if name is empty,
        // or as a suffix if name is provided.
        String dynamicName = "";
        for (Object arg : arguments) {
            if (arg instanceof String) {
                dynamicName = (String) arg;
                break;
            }
        }

        if (name.isEmpty()) {
            if (!dynamicName.isEmpty()) {
                name = dynamicName;
            } else {
                name = method.getDeclaringClass().getName() + "." + method.getName();
            }
        } else if (!dynamicName.isEmpty()) {
            name = name + "." + dynamicName;
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
        
        // Proceed with the actual method call
        Object result = invocation.proceed();
        
        // If the method returns a number, we might want to return the counter value instead?
        // "the result is the current counter"
        // If the method return type is compatible with long/Long, return the count.
        if (method.getReturnType().equals(long.class) || method.getReturnType().equals(Long.class)) {
            return counter.getCount();
        }
        if (method.getReturnType().equals(int.class) || method.getReturnType().equals(Integer.class)) {
            return (int) counter.getCount();
        }
        if (method.getReturnType().equals(double.class) || method.getReturnType().equals(Double.class)) {
            return (double) counter.getCount();
        }
        if (method.getReturnType().equals(float.class) || method.getReturnType().equals(Float.class)) {
            return (float) counter.getCount();
        }

        return result;
    }
}
