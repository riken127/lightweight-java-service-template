# Dependency Policy

This template stays useful by staying small.

## Default Position

Do not add a dependency unless it solves a concrete problem in the template or in the service being built from it.

Prefer:

- JDK APIs
- existing dependencies
- simple local code
- deletion

## Banned Dependencies

Do not add:

- Spring
- Quarkus
- Micronaut
- Dropwizard
- Hibernate
- JPA
- CDI
- Guice
- Dagger
- Lombok
- MapStruct
- large validation frameworks
- unnecessary OpenAPI tooling
- default Kubernetes manifests
- service mesh config
- default distributed tracing
- default authentication implementation

## Baseline Dependencies

Runtime:

- Javalin: lightweight HTTP server.
- Jackson: JSON mapping.
- jOOQ: explicit SQL access.
- HikariCP: JDBC connection pooling.
- PostgreSQL JDBC driver: PostgreSQL connectivity.
- Flyway: schema migrations.
- SLF4J and Logback: logging.

Test:

- JUnit 5: test framework.
- AssertJ: fluent assertions.
- Testcontainers: realistic PostgreSQL integration tests.

Build:

- Spotless: Google Java Format enforcement.
- Checkstyle: Google lint rules.

## Dependency Decision Template

Use this in PR descriptions or final summaries when adding a dependency:

```md
Dependency: groupId:artifactId
Scope: runtime/test/build
Problem solved:
Why existing stack was insufficient:
Why this dependency is lightweight enough:
Alternatives rejected:
Tests/docs updated:
```

## When Code Generation Is Acceptable

jOOQ code generation can be introduced when:

- the schema is stable enough to benefit from generated types
- generation is documented
- local setup remains simple
- CI verifies generation or generated sources consistently

Do not introduce code generation just because jOOQ supports it.
