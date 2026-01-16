# 📊 GuicedEE Metrics

[![JDK](https://img.shields.io/badge/JDK-25%2B-0A7?logo=java)](https://openjdk.org/projects/jdk/25/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

A production-ready metrics solution for GuicedEE that seamlessly integrates Vert.x 5 Dropwizard metrics with the MicroProfile Metrics 5.1.1 API and Guice dependency injection.

## ✨ Features

- **MicroProfile Metrics 5.1.1 Alignment**: Full support for standard annotations (`@Counted`, `@Timed`) using a bridge to Dropwizard Metrics.
- **Vert.x 5 Integration**: Deep integration with Vert.x metrics infrastructure, allowing for easy monitoring of event bus, HTTP servers/clients, and pools.
- **Annotation-Driven Configuration**: Configure the global metric registry, JMX, and monitored endpoints via the `@MetricsOptions` annotation.
- **Extensible Architecture**: Add custom metric types and interceptors by implementing the `Metrics` SPI.
- **Programmatic Access**: Type-safe access to Vert.x metrics using comprehensive enumerations (e.g., `VertxMetrics`, `HttpServerMetrics`).
- **Graphite Reporting**: Built-in support for reporting metrics to Graphite.
- **Prometheus Scrapping**: Built-in support for Prometheus scrapping via a Vert.x Web endpoint.

## 📦 Install (Maven)

```xml
<dependency>
    <groupId>com.guicedee</groupId>
    <artifactId>guiced-metrics</artifactId>
</dependency>
```

## 🚀 Quick Start

### 1. Configure Metrics

Use the `@MetricsOptions` annotation on your application class or package to enable and configure metrics.

```java
@MetricsOptions(
    enabled = true,
    jmxEnabled = true,
    baseName = "my-app",
    monitoredHttpServerUris = {
        @Match(value = "/api/.*", type = Match.MatchType.REGEX, alias = "api-calls")
    },
    graphite = @GraphiteOptions(enabled = true, host = "graphite.local"),
    prometheus = @PrometheusOptions(enabled = true, endpoint = "/metrics")
)
public class MyApplication {
}
```

### 2. Annotate Methods

Use standard MicroProfile Metrics annotations to collect data.

```java
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

public class MyService {

    @Counted(name = "items-processed", tags = {"env=prod"})
    public void processItem() {
        // ... logic
    }

    @Timed(name = "processing-time")
    public void longRunningTask() {
        // ... logic
    }
}
```

### 3. Access Vert.x Metrics Programmatically

Use the provided enums to retrieve metrics from the `MetricRegistry`.

```java
@Inject
MetricRegistry registry;

public void checkStats() {
    long activeConnections = registry.getCounters()
        .get(HttpServerMetrics.CONNECTIONS.toString())
        .getCount();
}
```

## 🧩 Custom Extensions

Register custom annotations and interceptors by implementing the `Metrics` interface and providing it via `ServiceLoader`.

```java
public class MyCustomMetrics implements Metrics {
    @Override
    public Map<Class<? extends Annotation>, Class<? extends MethodInterceptor>> annotations() {
        return Map.of(MyCustomAnnotation.class, MyCustomInterceptor.class);
    }
}
```

## 📊 Monitoring with Grafana & Graphite

A `docker-compose.yml` is provided to quickly spin up a monitoring stack.

### 1. Start the Stack

```bash
docker-compose up -d
```

This will start:
- **Graphite**: Accessible at `http://localhost:8080` (Web UI) and port `2003` (Pickle protocol).
- **Grafana**: Accessible at `http://localhost:3000` (Default login: `admin` / `admin`).

### 2. Configure Grafana

1.  Login to Grafana at `http://localhost:3000`.
2.  Go to **Connections** > **Data Sources**.
3.  Click **Add data source** and select **Graphite**.
4.  Set the **URL** to `http://graphite:80`.
5.  Click **Save & Test**.

### 3. Configure Your Application

Enable Graphite reporting in your `MetricsOptions` annotation.

### 4. Configure Prometheus

Enable Prometheus scrapping in your `MetricsOptions` annotation.

```java
@MetricsOptions(prometheus = @PrometheusOptions(enabled = true, endpoint = "/metrics"))
```

The endpoint will be automatically registered on the Vert.x router if `guiced-vertx-web` is present.

## 📚 Docs & Rules
- Rules: `RULES.md`
- Guides: `GUIDES.md`
- Architecture: `docs/architecture/README.md`

## 🤝 Contributing
Contributions are welcome! Please follow the existing code style and ensure all tests pass before submitting a PR.

## 📝 License
Apache 2.0 — see `LICENSE`.
