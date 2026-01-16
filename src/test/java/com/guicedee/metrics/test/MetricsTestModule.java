package com.guicedee.metrics.test;

import com.google.inject.AbstractModule;
import com.guicedee.client.services.lifecycle.IGuiceModule;

public class MetricsTestModule extends AbstractModule implements IGuiceModule<MetricsTestModule> {

    @Override
    protected void configure() {
        bind(TestService.class).asEagerSingleton();
    }
}
