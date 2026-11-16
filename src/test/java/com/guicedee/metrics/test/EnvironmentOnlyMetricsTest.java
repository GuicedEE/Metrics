package com.guicedee.metrics.test;

import com.guicedee.client.Environment;
import com.guicedee.metrics.implementations.MetricsPreStartup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class EnvironmentOnlyMetricsTest {
    @TempDir Path directory;

    @Test void enablesPrometheusWithoutAnnotationsAndHonorsOverrides() throws Exception {
        Properties original = (Properties) System.getProperties().clone();
        var previousOptions = MetricsPreStartup.getOptions();
        try {
            System.getProperties().keySet().removeIf(key -> key.toString().startsWith("METRICS_"));
            System.setProperty("user.dir", directory.toString());
            System.setProperty("GUICEDEE_DISCOVERY_MODE", "spi");
            Files.writeString(directory.resolve(".env"), "METRICS_PROMETHEUS_ENABLED=true\nMETRICS_JMX_ENABLED=false\nMETRICS_PROMETHEUS_ENDPOINT=/base\n");
            Files.writeString(directory.resolve(".env.local"), "METRICS_PROMETHEUS_ENDPOINT=/local\n");
            Environment.reloadDotEnv();
            new MetricsPreStartup().onStartup();
            var options = MetricsPreStartup.getOptions();
            assertTrue(options.prometheus().enabled());
            assertFalse(options.jmxEnabled());
            assertEquals("/local", options.prometheus().endpoint());
            System.setProperty("METRICS_PROMETHEUS_ENDPOINT", "/override");
            assertEquals("/override", options.prometheus().endpoint());
        } finally {
            System.setProperties(original);
            Environment.reloadDotEnv();
            var field = MetricsPreStartup.class.getDeclaredField("options");
            field.setAccessible(true);
            field.set(null, previousOptions);
        }
    }
}
