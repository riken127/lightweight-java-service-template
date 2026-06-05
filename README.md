# Lightweight Java Service Template

A GitHub template repository for small Java microservices that need a clean starting point without adopting a large application framework.

This is a template, not a framework. Click **Use this template**, initialize the service name/package, run the tests, and start replacing the tiny technical example with your service code.

Suggested GitHub description:

```txt
Lightweight Java 25 microservice template with Maven, Javalin, gRPC, jOOQ, PostgreSQL, Flyway, Testcontainers, and Google Java Style.
```

Suggested GitHub topics:

```txt
java, microservice-template, maven, javalin, grpc, protobuf, jooq, postgresql, flyway, testcontainers, google-java-format, no-spring
```

## Quick Start

```bash
scripts/init-template.sh \
  --service-name orders-service \
  --package com.acme.orders \
  --group-id com.acme

make format
make test
```

Start local PostgreSQL and run the service:

```bash
make postgres-up
make run
```

Smoke-test HTTP:

```bash
make smoke
```

The service listens on:

- HTTP: `8080`
- gRPC: `9090`

## What You Get

- Java 25
- Maven Wrapper
- Javalin HTTP
- direct grpc-java with protobuf code generation
- Jackson JSON
- jOOQ, HikariCP, PostgreSQL JDBC, and Flyway
- SLF4J + Logback
- JUnit 5, AssertJ, and Testcontainers
- Google Java Format and Google Checkstyle
- Dockerfile and Docker Compose PostgreSQL
- GitHub Actions CI
- AI-oriented repository guidance in `AGENTS.md` and `.agents/`

## Structure

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

Package boundaries:

```txt
com.example.service
├── bootstrap/
├── config/
├── http/
├── grpc/
├── application/
├── domain/
├── persistence/
└── observability/
```

## Documentation

- [Template usage](docs/template-usage.md): initialization, local development, tests, routes, gRPC methods, repositories, migrations, and containers.
- [Architecture](docs/architecture.md): philosophy, structure decision, package boundaries, dependency choices, and intentional omissions.
- [Agent guidance](AGENTS.md): rules for AI agents and maintainers working in this repository.

## Common Commands

```bash
make help
make format
make grpc-generate
make test
make verify
make docker-build
```

Maven remains the source of truth:

```bash
./mvnw test
./mvnw clean verify
```

## Template Philosophy

This repository is intentionally small. It favours explicit wiring, simple runtime behavior, low dependency count, fast tests, clear package boundaries, easy local development, and easy production hardening.

It avoids framework magic, premature modularisation, fake enterprise architecture, unnecessary annotations, unnecessary runtime reflection, and platform tooling that a small service may not actually use.

See [docs/architecture.md](docs/architecture.md) for the full architecture decision record.
