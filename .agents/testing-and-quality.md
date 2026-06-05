# Testing and Quality

Maven is the source of truth. The Makefile is a convenience layer.

## Commands

```bash
make format
make format-check
make lint
make grpc-generate
make test
make integration-test
make verify
make smoke
make docker-build
```

Equivalent Maven commands:

```bash
./mvnw spotless:apply
./mvnw spotless:check
./mvnw checkstyle:check -DskipTests -DskipITs
./mvnw -pl app protobuf:compile protobuf:compile-custom
./mvnw test
./mvnw -DskipTests failsafe:integration-test failsafe:verify
./mvnw clean verify
```

## Test Naming

- Fast tests: `*Test`
- Integration tests: `*IntegrationTest`

Surefire runs fast tests. Failsafe runs integration tests.

## Test Style

- Prefer AssertJ.
- Prefer real objects and small fakes.
- Avoid Mockito unless interaction verification is genuinely clearer.
- Keep HTTP tests black-box at the route level.
- Keep gRPC adapter tests in-process with generated stubs.
- Keep database tests realistic with Testcontainers.

## Quality Gates

`make verify` must pass:

- compilation
- protobuf and gRPC stub generation
- fast tests
- integration tests
- jar packaging
- Spotless Google Java Format check
- Google Checkstyle

Checkstyle warnings are build failures. Do not ignore them.

## Docker Checks

The Dockerfile is part of the template. When changing packaging, Maven coordinates, runtime classpath, or Java versions, run:

```bash
make docker-build
```

The image expects environment variables matching `.env.example`.

## Formatting

This repository uses Google Java Format through Spotless. Do not manually fight the formatter.

Use:

```bash
make format
```

Then inspect the result for readability.
