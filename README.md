# Lightweight Java Service Template

A GitHub template repository for small Java microservices that need a clear starting point without adopting a large application framework.

This is a template, not a framework. Click **Use this template**, rename the service, set your package name, run the tests, and start replacing the tiny technical example with your service code.

Suggested GitHub description:

```txt
Lightweight Java 25 microservice template with Maven, Javalin, jOOQ, PostgreSQL, Flyway, Testcontainers, and Google Java Style.
```

Suggested GitHub topics:

```txt
java, microservice-template, maven, javalin, jooq, postgresql, flyway, testcontainers, google-java-format, no-spring
```

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

## What this is

- A Maven-based Java 25 service template.
- A single deployable Javalin HTTP service.
- A minimal PostgreSQL data-access pattern using HikariCP, Flyway, and jOOQ.
- A small testing setup with fast unit/HTTP tests and Testcontainers integration tests.
- A baseline for local development, CI, logging, graceful shutdown, and health/readiness checks.

## What this is not

- It is not a framework.
- It is not a sample product.
- It is not a monorepo platform.
- It does not include Spring, Quarkus, Micronaut, Dropwizard, Hibernate, Lombok, MapStruct, Kubernetes, service mesh config, OpenAPI tooling, or authentication.

## Architecture Decision

The chosen structure is a root Maven parent with one deployable `app` module:

```txt
.
├── app/
│   ├── pom.xml
│   └── src/
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

The research inspiration was mature service ownership practice rather than company-specific layouts: independent service ownership and deployments, docs with code, simple templates, and clear operational boundaries. Useful references include Uber's discussion of clear service ownership in domain-oriented microservices, Spotify/Backstage's emphasis on templates and catalogs, Airbnb's SOA ownership lessons, and Netflix's platform focus on service dependency visibility.

## Package Layout

```txt
com.example.service
├── Main.java
├── bootstrap/
├── config/
├── http/
├── application/
├── domain/
├── persistence/
└── observability/
```

- `bootstrap`: application wiring, dependency construction, migrations, server startup, graceful shutdown.
- `config`: environment variable parsing and validation.
- `http`: Javalin routes, DTOs, request/response mapping, central error mapping.
- `application`: use-case orchestration and repository interfaces.
- `domain`: framework-free domain records and rules.
- `persistence`: jOOQ and database-specific repository implementations.
- `observability`: request correlation and readiness contracts.

## Dependency Choices

- Java 25: current baseline for this template.
- Maven: predictable Java build tool, easy CI caching, widely understood by Java teams.
- Javalin: lightweight HTTP layer without a full application framework.
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

That is deliberate: a new service should be runnable after `Use this template` without requiring a local database and code-generation step before the first edit. For larger services, introduce jOOQ code generation once the schema stabilizes and document the command in this README.

Rules:

- Keep jOOQ usage inside `persistence`.
- Do not expose jOOQ records outside `persistence`.
- Map database rows to domain records before returning from repositories.

## Configuration

Configuration is read from environment variables with safe local defaults:

| Variable | Default |
| --- | --- |
| `SERVICE_NAME` | `service-template` |
| `HTTP_PORT` | `8080` |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/service_template` |
| `DATABASE_USERNAME` | `service_template` |
| `DATABASE_PASSWORD` | `service_template` |
| `DATABASE_MAX_POOL_SIZE` | `5` |
| `LOG_LEVEL` | `INFO` |

Do not commit real secrets. Use your deployment platform's secret manager or environment injection.

Copy `.env.example` if you want a local shell reference, but do not commit real `.env` files.

## Run Locally

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the service:

```bash
./mvnw -pl app exec:java
```

Or use the Makefile convenience targets:

```bash
make postgres-up
make run
```

Then call:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/ready
curl http://localhost:8080/v1/example
```

You can also smoke-test the default local endpoints:

```bash
make smoke
```

## Run Tests

Fast unit and HTTP tests:

```bash
./mvnw test
```

Full build, including Testcontainers PostgreSQL integration tests, Google Java Format, and Google Checkstyle:

```bash
./mvnw clean verify
```

## GitHub Template Usage

1. Mark this repository as a template in GitHub repository settings.
2. Click **Use this template**.
3. Rename the repository and Maven coordinates.
4. Rename `com.example.service` to your package.
5. Update `SERVICE_NAME`, module artifact ids, README text, and CI branch names if needed.
6. Run `./mvnw test`.
7. Start PostgreSQL with `docker compose up -d postgres`.
8. Run `./mvnw -pl app exec:java`.

Package rename checklist:

- Move `app/src/main/java/com/example/service`.
- Move `app/src/test/java/com/example/service`.
- Update package declarations and imports.
- Update `exec-maven-plugin` `mainClass` in the root `pom.xml`.

## Add a Route

1. Add a route class or method in `http`.
2. Add DTO records under `http/dto` if the route returns JSON.
3. Register the route in `HttpServerFactory`.
4. Put orchestration in `application`, not in the route handler.
5. Add an HTTP test.

## Add an Application Service

1. Create a service class in `application`.
2. Depend on domain types and repository interfaces.
3. Keep Javalin and jOOQ out of the service.
4. Wire it in `ApplicationBootstrap`.
5. Add a unit test that does not require PostgreSQL.

## Add a Repository

1. Add a repository interface in `application`.
2. Add the jOOQ-backed implementation in `persistence`.
3. Map jOOQ results to domain records before returning.
4. Add a Testcontainers integration test if behavior depends on SQL.

## Add a Migration

Add a new file under:

```txt
app/src/main/resources/db/migration
```

Use Flyway naming:

```txt
V2__describe_change.sql
```

The application runs migrations during bootstrap. Integration tests run the same migrations against PostgreSQL containers.

## HTTP Endpoints

- `GET /health`: process liveness only.
- `GET /ready`: checks dependencies needed to serve traffic, currently PostgreSQL.
- `GET /v1/example`: tiny technical repository example. Replace or delete it when building a real service.

Errors return JSON with:

```json
{
  "code": "bad_request",
  "message": "limit must be greater than zero",
  "requestId": "..."
}
```

## Logging

Logback writes to stdout and includes timestamp, level, thread, logger, message, and request id. Request correlation uses the `X-Request-Id` header when present, otherwise it generates one.

## CI

GitHub Actions runs:

```bash
./mvnw -B clean verify
```

This includes unit tests, HTTP tests, and Testcontainers integration tests.

## Formatting and Linting

This template follows Google Java Style, including 2-space indentation. Maven enforces it during `verify`:

- Spotless runs `google-java-format`.
- Checkstyle runs `google_checks.xml`.

Format code locally with:

```bash
./mvnw spotless:apply
```

The same workflow is available through:

```bash
make format
make lint
make verify
```

## Container Image

Build a local image:

```bash
make docker-build
```

Run it against the local Docker Compose PostgreSQL service from the host network as appropriate for your Docker environment. The image expects the same environment variables documented above.

Useful JVM runtime override:

```bash
JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25"
```

The container image is intentionally minimal: no Kubernetes manifests, Helm charts, service mesh configuration, or registry publishing workflow are included.

## Intentional Omissions

- No dependency injection framework: manual constructor wiring is enough here.
- No generated jOOQ classes: avoids a mandatory codegen step for first use.
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
- Application services orchestrate work and define repository ports.
- Persistence is the only package that knows database details.
- Domain code stays framework-free.
- Tests should be readable and proportional to risk.
- Prefer deleting the example code over generalizing it prematurely.

## Repository Governance

This template includes:

- `LICENSE`: MIT license for reuse.
- `CONTRIBUTING.md`: contribution workflow.
- `SECURITY.md`: vulnerability reporting expectations.
- `CODEOWNERS`: default ownership placeholder.
- Dependabot configuration for Maven, GitHub Actions, and Docker.
- Pull request and issue templates.
- `.agents/`: AI-oriented repository guidance.

Recommended branch protection for GitHub:

- Require pull requests before merging.
- Require the `CI` workflow.
- Require branches to be up to date before merging.
- Require CODEOWNERS review if using this template in an organization.

## External Inspiration

- Uber Engineering: https://www.uber.com/blog/microservice-architecture/
- Backstage/Spotify: https://backstage.io/docs/overview/technical-overview
- Backstage templates and ownership: https://backstage.io/blog/2020/03/18/what-is-backstage/
- Airbnb Engineering SOA data ownership: https://www.engineering.fyi/article/capturing-data-evolution-in-a-service-oriented-architecture
- Netflix service topology visibility: https://netflixtechblog.com/from-silos-to-service-topology-why-netflix-built-a-real-time-service-map-0165ba13a7bc
