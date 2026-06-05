# Change Playbook

Use the smallest workflow that fits the change.

## Add a New HTTP Endpoint

1. Define response/request records under `http/dto`.
2. Add route logic under `http`.
3. Keep request parsing and response mapping in `http`.
4. Put decisions and orchestration in `application`.
5. Register the route in `HttpServerFactory`.
6. Add an HTTP test.
7. Run `make format`.
8. Run `make test`.

Avoid:

- SQL in handlers.
- Business decisions in handlers.
- Returning domain records directly when the HTTP shape is likely to diverge.

## Add a New gRPC Method

1. Add or update a proto contract under `app/src/main/proto`.
2. Generate sources with `make grpc-generate` or run any Maven compile/test command.
3. Implement the generated service base class under `grpc`.
4. Keep protobuf-to-domain mapping in `grpc`.
5. Put decisions and orchestration in `application`.
6. Register the service in `GrpcServerFactory`.
7. Add an in-process gRPC test.
8. Run `make format`.
9. Run `make test`.

Avoid:

- Business decisions in gRPC service classes.
- Passing protobuf messages into `application` or `domain`.
- Depending on generated gRPC classes from persistence code.
- Adding a gRPC wrapper framework.

## Add a New Use Case

1. Add an application service under `application`.
2. Add repository interfaces under `application` only when persistence is needed.
3. Add domain records or rules under `domain`.
4. Wire the service in `ApplicationBootstrap`.
5. Add unit tests that use real objects or small fakes.

Avoid:

- Mocking simple records or simple repository fakes.
- Pulling Javalin context into the application layer.
- Adding a generic command bus, mediator, or service locator.

## Add Database Access

1. Add a Flyway migration in `app/src/main/resources/db/migration`.
2. Add or update an application repository interface.
3. Implement with jOOQ in `persistence`.
4. Keep table and field definitions private to persistence code unless jOOQ generation is intentionally introduced.
5. Add a `*IntegrationTest` using Testcontainers.
6. Run `make verify`.

Avoid:

- Returning jOOQ records.
- Throwing raw database exceptions across package boundaries when a safer application exception is warranted.
- Adding code generation before the schema has enough weight to justify it.

## Change Configuration

1. Add the field to a config record.
2. Parse it in `EnvironmentConfig`.
3. Validate in the record constructor.
4. Use it from `ApplicationBootstrap`.
5. Document it in `README.md`.

Safe defaults are fine for local development. Secrets must come from environment or deployment secret management.

## Add a Dependency

1. Read `dependency-policy.md`.
2. Prefer not adding it.
3. If still needed, add it in the narrowest scope.
4. Explain why in the final response and relevant docs.
5. Add tests that prove the dependency earns its place.

## Remove Example Code

When turning the template into a real service, deleting example code is encouraged:

- Delete `/v1/example`.
- Delete the `ExampleItemsApi` proto and gRPC adapter if the service does not need gRPC.
- Delete example DTOs.
- Delete `ExampleService`.
- Delete `ExampleItem`.
- Delete example repository code.
- Replace the example migration with real migrations.

Do not generalize the example into a framework.
