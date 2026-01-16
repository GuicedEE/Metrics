package com.guicedee.metrics.test;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CustomMetricInterceptor implements MethodInterceptor {
    public static boolean called = false;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        called = true;
        return invocation.proceed();
    }
}
