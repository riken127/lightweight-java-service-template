# Testing and Quality

Maven is the source of truth. The Makefile is a convenience layer.

## Commands

```bash
make format
make format-check
make lint
make test
make integration-test
make verify
```

Equivalent Maven commands:

```bash
./mvnw spotless:apply
./mvnw spotless:check
./mvnw checkstyle:check -DskipTests -DskipITs
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
- Keep database tests realistic with Testcontainers.

## Quality Gates

`make verify` must pass:

- compilation
- fast tests
- integration tests
- jar packaging
- Spotless Google Java Format check
- Google Checkstyle

Checkstyle warnings are build failures. Do not ignore them.

## Formatting

This repository uses Google Java Format through Spotless. Do not manually fight the formatter.

Use:

```bash
make format
```

Then inspect the result for readability.
