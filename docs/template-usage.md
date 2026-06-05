# Template Usage

Use this document after creating a repository from the GitHub template.

## Create a Service from the Template

1. Mark this repository as a template in GitHub repository settings.
2. Click **Use this template**.
3. Clone the new repository.
4. Run the initialization script:

```bash
scripts/init-template.sh \
  --service-name orders-service \
  --package com.acme.orders \
  --group-id com.acme
```

5. Review the changed files.
6. Run:

```bash
make format
make test
```

7. Commit the initialized service.

## Initialization Script

`scripts/init-template.sh` updates the common template placeholders:

- Java package declarations.
- Java package directory paths.
- Protobuf `java_package`.
- Maven `groupId`.
- Maven root and app artifact ids.
- `SERVICE_NAME`.
- local database names derived from the service name.
- documentation references.

Preview changes without editing files:

```bash
scripts/init-template.sh \
  --service-name orders-service \
  --package com.acme.orders \
  --dry-run
```

The script is intentionally simple shell code. It does not try to infer product-specific names, deployment settings, auth, tracing, or messaging.

## Configuration

Configuration is read from environment variables with safe local defaults:

| Variable | Default |
| --- | --- |
| `SERVICE_NAME` | `service-template` |
| `HTTP_PORT` | `8080` |
| `GRPC_PORT` | `9090` |
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

Or use Make:

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

The gRPC server listens on `9090` by default and exposes standard gRPC health and reflection services. The example service contract lives at:

```txt
app/src/main/proto/com/example/service/v1/example_items.proto
```

Smoke-test the default HTTP endpoints:

```bash
make smoke
```

## Run Tests

Fast unit, HTTP, and in-process gRPC tests:

```bash
./mvnw test
make test
```

Full build, including Testcontainers PostgreSQL integration tests, Google Java Format, and Google Checkstyle:

```bash
./mvnw clean verify
make verify
```

Generate protobuf and gRPC sources explicitly:

```bash
make grpc-generate
```

Generated sources live under `target/` and should not be committed.

## Add an HTTP Route

1. Add a route class or method in `http`.
2. Add DTO records under `http/dto` if the route returns JSON.
3. Register the route in `HttpServerFactory`.
4. Put orchestration in `application`, not in the route handler.
5. Add an HTTP test.

## Add a gRPC Method

1. Add or update a `.proto` file under `app/src/main/proto`.
2. Run `make grpc-generate` or any Maven compile/test command.
3. Implement the generated service base class in `grpc`.
4. Map protobuf messages to application inputs and domain outputs.
5. Keep business rules in `application`, not in the gRPC service implementation.
6. Register the service in `GrpcServerFactory`.
7. Add an in-process gRPC test.

## Add an Application Service

1. Create a service class in `application`.
2. Depend on domain types and repository interfaces.
3. Keep Javalin, protobuf, gRPC, and jOOQ out of the service.
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

## Endpoints

HTTP:

- `GET /health`: process liveness only.
- `GET /ready`: checks dependencies needed to serve traffic, currently PostgreSQL.
- `GET /v1/example`: tiny technical repository example. Replace or delete it when building a real service.

gRPC:

```txt
example.service.v1.ExampleItemsApi/ListExampleItems
```

The gRPC method delegates to the same `ExampleService` used by HTTP.

## Formatting and Linting

This template follows Google Java Style, including 2-space indentation. Maven enforces it during `verify`:

- Spotless runs `google-java-format`.
- Checkstyle runs `google_checks.xml`.

Format code locally with:

```bash
make format
```

## Container Image

Build a local image:

```bash
make docker-build
```

The image exposes:

- `8080` for HTTP.
- `9090` for gRPC.

Useful JVM runtime override:

```bash
JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25"
```

The container image is intentionally minimal: no Kubernetes manifests, Helm charts, service mesh configuration, or registry publishing workflow are included.

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
