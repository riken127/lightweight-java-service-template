# Repository Map

This repository is one deployable Java service template with one Maven app module.

```txt
.
├── app/
│   ├── pom.xml
│   └── src/
├── pom.xml
├── Makefile
├── docker-compose.yml
├── README.md
├── AGENTS.md
└── .agents/
```

## Maven Shape

- Root `pom.xml`: shared versions, dependency management, plugin management, quality gate configuration.
- `app/pom.xml`: deployable service module and module-level plugin activation.

Do not add modules unless a real service need appears. A new module should remove more complexity than it adds.

## Runtime Flow

1. `Main` loads environment configuration.
2. `ApplicationBootstrap` constructs the dependency graph.
3. Flyway runs migrations.
4. HikariCP owns JDBC connections.
5. jOOQ creates the SQL DSL context.
6. Repositories and application services are constructed manually.
7. `HttpServerFactory` creates Javalin routes and error mapping.
8. `Application` starts Javalin and closes resources during shutdown.

## Package Boundaries

`bootstrap`

- Owns wiring and lifecycle.
- May depend on every package.
- Must not contain business logic.

`config`

- Owns environment parsing and validation.
- Must not read secrets from committed files.
- Should use local defaults only when safe.

`http`

- Owns Javalin handlers, DTOs, and error mapping.
- Must not contain business rules.
- Must not run SQL directly.

`application`

- Owns orchestration.
- Defines repository interfaces.
- Must not depend on Javalin.
- Should not depend on jOOQ.

`domain`

- Owns framework-free domain records and rules.
- Must not depend on HTTP, JSON, SQL, database, or logging frameworks.

`persistence`

- Owns jOOQ and database-specific mapping.
- Must not leak jOOQ records outside the package.
- Should translate database rows into domain records.

`observability`

- Owns request correlation and readiness contracts.
- Should stay lightweight and optional.

## Files Agents Commonly Touch

- Add route: `http`, `http/dto`, `HttpServerFactory`, HTTP tests.
- Add use case: `application`, `domain` if needed, unit tests.
- Add repository: `application` interface, `persistence` implementation, Flyway migration, integration test.
- Add config: `config`, `README.md`, possibly bootstrap wiring.
- Add dependency: `pom.xml`, `app/pom.xml`, `README.md`, `AGENTS.md` if policy changes.
