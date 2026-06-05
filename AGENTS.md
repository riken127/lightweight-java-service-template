# AGENTS.md

This repository is a lightweight Java microservice template. It is designed for agents and humans to change safely without turning the template into a framework, demo product, or platform monorepo.

Read this file first. Then read the focused guides under `.agents/` before changing code.

## Agent Knowledge Base

- `.agents/repository-map.md`: repository structure, package boundaries, and ownership rules.
- `.agents/change-playbook.md`: step-by-step workflows for common modifications.
- `.agents/testing-and-quality.md`: test strategy, Makefile targets, Maven checks, and verification expectations.
- `.agents/dependency-policy.md`: dependency rules, approved stack, and dependency decision template.
- `.agents/template-customization.md`: how to rename and adapt this repository after using it as a GitHub template.

## Repository Intent

This repository provides a minimal, production-oriented starting point for small Java services.

It should provide:

- explicit application bootstrap
- clear package boundaries
- minimal HTTP routing
- small database access example
- local PostgreSQL setup
- realistic tests
- CI and quality gates
- documentation that helps the next maintainer move quickly

It should not provide:

- a custom framework
- a fake business product
- a platform monorepo
- enterprise boilerplate
- unnecessary code generation
- deployment manifests for platforms the service does not actually use
- abstractions added only because a future service might need them

## Non-Negotiable Rules

1. Do not add Spring, Quarkus, Micronaut, Dropwizard, Hibernate, JPA, CDI, Guice, Dagger, Lombok, MapStruct, or large validation frameworks.
2. Do not add a dependency without explaining why in the change summary and, when relevant, in the README.
3. Do not move business logic into HTTP handlers.
4. Do not expose jOOQ records, generated tables, JDBC objects, or database exceptions outside `persistence`.
5. Do not make domain classes depend on Javalin, Jackson, jOOQ, JDBC, Flyway, HikariCP, or database APIs.
6. Do not introduce static service locators, global mutable registries, reflection-heavy wiring, or annotation-driven dependency injection.
7. Do not create fake business domains. The `example_items` table is a technical example only.
8. Do not add Kubernetes, service mesh, Backstage, OpenAPI, tracing, authentication, or authorization by default.
9. Do not replace Maven with Gradle.
10. Do not weaken tests, formatting, linting, or CI to make a change pass.
11. Keep the README updated when commands, structure, dependencies, or workflows change.
12. Prefer deletion over abstraction when code is not needed.
13. Optimise for clarity over cleverness.

## Coding Style

- Use Java 25.
- Follow Google Java Style with 2-space indentation.
- Run `make format` after editing Java files.
- Public types and public methods need concise Javadocs because Google Checkstyle enforces them.
- Keep comments useful and factual. Explain intent, boundaries, or non-obvious tradeoffs.
- Prefer records for immutable data carriers.
- Prefer small final classes with constructor dependencies.
- Prefer package-private route/helper classes unless they are part of a package boundary.
- Avoid inheritance unless Java or a library requires it.
- Avoid clever generic abstractions in template code.
- Avoid introducing null unless a framework API requires it. Validate at boundaries.

## Architecture Boundaries

The package boundary is part of the template contract:

- `bootstrap`: dependency construction, Flyway migration, Javalin startup, graceful shutdown.
- `config`: environment variable parsing and validation.
- `http`: Javalin routes, DTOs, request mapping, response mapping, error mapping.
- `application`: use-case orchestration and repository interfaces.
- `domain`: framework-free domain records and rules.
- `persistence`: jOOQ usage, SQL mapping, database-specific implementations.
- `observability`: request correlation, readiness contracts, logging support.

Dependency direction:

- `http` may depend on `application`, DTOs, and observability helpers.
- `application` may depend on `domain` and repository interfaces.
- `domain` must not depend on other application packages.
- `persistence` may depend on `application` interfaces and `domain` records.
- `bootstrap` may depend on every package because it wires the graph.

When in doubt, keep the dependency closer to the outside edge. Domain code should stay boring and isolated.

## Testing Rules

- `make test` must run fast tests only.
- `make verify` must run the full suite, including integration tests, formatting, and linting.
- Unit tests must not require PostgreSQL or Docker.
- HTTP tests should start Javalin on a random port.
- Database tests should use Testcontainers and be named `*IntegrationTest`.
- Use AssertJ for assertions.
- Avoid Mockito unless a real object or small fake would make the test meaningfully worse.
- Add or update tests when changing behavior.
- Do not hide broken tests behind profiles unless the README explains the workflow.

## Maven and Build Rules

- Keep Maven plugin usage minimal and explicit.
- Quality gates belong in `verify`.
- Use the Maven Wrapper in documentation and automation.
- `Makefile` targets are convenience wrappers; Maven remains the source of truth.
- Do not introduce parent POM complexity beyond what this template needs.
- Do not add formatting rules that fight Google Java Format.

## Dependency Rules

Before adding a dependency, answer:

1. What problem does this solve?
2. Why is the JDK or current stack insufficient?
3. Is it runtime, test-only, or build-only?
4. Does it preserve the template's lightweight posture?
5. What code or dependency can be removed instead?

Allowed by default because they are part of the template baseline:

- Javalin
- Jackson
- jOOQ
- HikariCP
- PostgreSQL JDBC driver
- Flyway
- SLF4J
- Logback
- JUnit 5
- AssertJ
- Testcontainers
- Spotless
- Checkstyle

Everything else needs justification.

## Common Change Patterns

Adding a route:

1. Add request/response DTOs under `http/dto` if JSON shape is needed.
2. Add route handling in `http`.
3. Put orchestration in `application`.
4. Put persistence behind an application repository interface.
5. Register wiring in `bootstrap`.
6. Add or update HTTP tests.

Adding database behavior:

1. Add a Flyway migration.
2. Add or update a repository interface in `application`.
3. Implement jOOQ code in `persistence`.
4. Map jOOQ results to domain records before returning.
5. Add a Testcontainers integration test.

Changing configuration:

1. Add a field to the relevant config record.
2. Load it in `EnvironmentConfig`.
3. Validate it close to the config record.
4. Document the environment variable in the README.
5. Add a test if parsing or validation is non-trivial.

## Commit Rules

Use Conventional Commits:

- `feat:` for user-visible capability or template feature
- `fix:` for bug fixes
- `build:` for build system, Maven, Makefile, or CI changes
- `test:` for test-only changes
- `docs:` for README, AGENTS, or `.agents` guidance
- `style:` for formatting-only changes
- `refactor:` for behavior-preserving code restructuring
- `chore:` for maintenance that does not fit the above

Keep commits atomic:

- One reason per commit.
- Do not mix unrelated formatting, docs, and behavior.
- Run the relevant checks before committing.

## Final Checklist Before Hand-Off

- `make format` was run after Java edits.
- `make verify` passes, unless the user explicitly asked for a narrower check.
- README is still accurate.
- `.agents/` guidance is still accurate.
- No local secrets or machine-specific files are staged.
- No banned dependencies were introduced.
- The change still feels like a template, not an application framework.
