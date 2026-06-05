# Template Customization

Use this guide after clicking **Use this template** on GitHub.

## Rename the Service

1. Update Maven coordinates in `pom.xml` and `app/pom.xml`.
2. Update `SERVICE_NAME` defaults in `EnvironmentConfig`.
3. Update README references.
4. Update Docker Compose database names if desired.
5. Run `make verify`.

## Rename the Package

Default package:

```txt
com.example.service
```

Rename both source roots:

```txt
app/src/main/java/com/example/service
app/src/test/java/com/example/service
```

Then update:

- package declarations
- imports
- `exec-maven-plugin` `mainClass`

Run:

```bash
make format
make verify
```

## Replace the Technical Example

The example route and table exist only to show structure.

Delete or replace:

- `GET /v1/example`
- `ExampleRoutes`
- example DTOs
- `ExampleService`
- `ExampleItemRepository`
- `ExampleItem`
- `JooqExampleItemRepository`
- `V1__create_example_items.sql`
- example tests

Keep:

- package boundaries
- config style
- bootstrap style
- error handling
- health/readiness
- request correlation

## Production Hardening Checklist

Add only what your runtime environment actually needs:

- deployment packaging
- secret injection
- metrics
- tracing
- authentication
- authorization
- OpenAPI publishing
- database migration policy
- container image build

Document every addition. Keep optional platform concerns out of the baseline template.
