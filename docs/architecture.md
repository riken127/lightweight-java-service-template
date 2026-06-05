# Architecture

This document captures the template philosophy, structural decisions, package boundaries, dependency choices, and intentional omissions.

## Philosophy

This template is intentionally small.

It is designed for teams that want lightweight Java services without adopting a large application framework.

The template favours:

- explicit wiring
- simple runtime behaviour
- low dependency count
- fast tests
- clear package boundaries
- easy local development
- easy production hardening

It avoids:

- framework magic
- premature modularisation
- fake enterprise architecture
- unnecessary annotations
- unnecessary runtime reflection

## What This Is

- A Maven-based Java 25 service template.
- A single deployable service with Javalin HTTP and direct grpc-java endpoints.
- A minimal PostgreSQL data-access pattern using HikariCP, Flyway, and jOOQ.
- A small testing setup with fast unit/HTTP/gRPC tests and Testcontainers integration tests.
- A baseline for local development, CI, logging, graceful shutdown, and health/readiness checks.

## What This Is Not

- It is not a framework.
- It is not a sample product.
- It is not a monorepo platform.
- It does not include Spring, Quarkus, Micronaut, Dropwizard, Hibernate, Lombok, MapStruct, Kubernetes, service mesh config, OpenAPI tooling, or authentication.

## Repository Structure Decision

The chosen structure is a root Maven parent with one deployable `app` module:

```txt
.
├── app/
│   ├── pom.xml
│   └── src/
├── docs/
├── scripts/
├── pom.xml
├── Makefile
├── docker-compose.yml
├── Dockerfile
└── README.md
```

This is the smallest structure that still supports professional growth. It is simpler than a monorepo-style `services/` layout, keeps one GitHub template equal to one deployable service, and avoids implying shared libraries before a team actually needs them. The root parent keeps Java, plugin, and dependency versions in one place while the `app` module remains the only deployable artifact.

The alternatives were intentionally not chosen:

- Flat single-service repo: simplest, but gives no clean Maven growth path.
- Multi-module layered repo: too much build-level ceremony for a lightweight template.
- Monorepo or hybrid `services/` repo: useful for platform teams, but too suggestive for a GitHub microservice template.

The research inspiration was mature service ownership practice rather than company-specific layouts: independent service ownership and deployments, docs with code, simple templates, and clear operational boundaries.

## Package Layout

```txt
com.example.service
├── Main.java
├── bootstrap/
├── config/
├── http/
├── grpc/
├── application/
├── domain/
├── persistence/
└── observability/
```

- `bootstrap`: application wiring, dependency construction, migrations, server startup, graceful shutdown.
- `config`: environment variable parsing and validation.
- `http`: Javalin routes, DTOs, request/response mapping, central error mapping.
- `grpc`: protobuf service adapters, request mapping, gRPC status mapping, interceptors, health, reflection, and gRPC server setup.
- `application`: use-case orchestration and repository interfaces.
- `domain`: framework-free domain records and rules.
- `persistence`: jOOQ and database-specific repository implementations.
- `observability`: request correlation and readiness contracts.

## Dependency Direction

- `http` may depend on `application`, DTOs, and observability helpers.
- `grpc` may depend on generated protobuf types, `application`, and observability helpers.
- `application` may depend on `domain` and repository interfaces.
- `domain` must not depend on other application packages.
- `persistence` may depend on `application` interfaces and `domain` records.
- `bootstrap` may depend on every package because it wires the graph.

Domain code should stay boring and isolated.

## Dependency Choices

- Java 25: current baseline for this template.
- Maven: predictable Java build tool, easy CI caching, widely understood by Java teams.
- Javalin: lightweight HTTP layer without a full application framework.
- grpc-java: direct RPC support without adding a second application framework.
- Protobuf Maven Plugin: generates protobuf messages and grpc-java service stubs from `src/main/proto`.
- Jackson: JSON serialization and Java time support.
- jOOQ: explicit SQL access with a fluent API.
- HikariCP: small, mature JDBC connection pool.
- PostgreSQL JDBC driver: PostgreSQL connectivity.
- Flyway: simple migration lifecycle for local, test, and production startup.
- SLF4J + Logback: standard logging facade and runtime implementation.
- JUnit 5: modern Java test framework.
- AssertJ: readable assertions.
- Testcontainers: realistic PostgreSQL integration tests in CI.

Mockito is intentionally omitted because the current tests are clearer with real objects and small fakes.

## jOOQ Decision

This template uses handwritten jOOQ table and field references instead of generated jOOQ classes.

That is deliberate: a new service should be runnable after `Use this template` without requiring a local database and code-generation step before the first edit. For larger services, introduce jOOQ code generation once the schema stabilizes and document the command.

Rules:

- Keep jOOQ usage inside `persistence`.
- Do not expose jOOQ records outside `persistence`.
- Map database rows to domain records before returning from repositories.

## gRPC Decision

The template uses grpc-java directly rather than a gRPC microframework.

The default gRPC workflow uses:

- `grpc-netty-shaded` for runtime HTTP/2 transport.
- `grpc-protobuf` and `grpc-stub` for generated protobuf services.
- `grpc-services` for reflection support and standard gRPC service definitions.
- `grpc-inprocess` for fast adapter tests without binding sockets.

The gRPC adapter delegates to the same application service used by HTTP. Generated protobuf types stay at the adapter boundary and do not enter `application`, `domain`, or `persistence`.

## Intentional Omissions

- No dependency injection framework: manual constructor wiring is enough here.
- No generated jOOQ classes: avoids a mandatory codegen step for first use.
- No gRPC framework wrapper: direct grpc-java keeps RPC behavior explicit.
- No messaging broker by default: add one only when the service has a concrete asynchronous workflow.
- No OpenAPI tooling: add it when an API contract needs publishing.
- No Kubernetes manifests: deployment targets vary too much for a minimal template.
- No authentication: security should be service- and platform-specific.
- No distributed tracing: add OpenTelemetry when the runtime environment supports it.
- No custom formatter rules: Google Java Format and Google Checkstyle are used as-is.
- No registry publishing workflow: image registries and release processes vary by team.

## Design Principles

- One repository represents one deployable service.
- The service owns its schema migrations.
- HTTP handlers translate requests; they do not own business rules.
- gRPC services translate protobuf calls; they do not own business rules.
- Application services orchestrate work and define repository ports.
- Persistence is the only package that knows database details.
- Domain code stays framework-free.
- Tests should be readable and proportional to risk.
- Prefer deleting the example code over generalizing it prematurely.

## External Inspiration

- Uber Engineering: https://www.uber.com/blog/microservice-architecture/
- Backstage/Spotify: https://backstage.io/docs/overview/technical-overview
- Backstage templates and ownership: https://backstage.io/blog/2020/03/18/what-is-backstage/
- Airbnb Engineering SOA data ownership: https://www.engineering.fyi/article/capturing-data-evolution-in-a-service-oriented-architecture
- Netflix service topology visibility: https://netflixtechblog.com/from-silos-to-service-topology-why-netflix-built-a-real-time-service-map-0165ba13a7bc
