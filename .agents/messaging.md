# Messaging Guidance

This template does not include a broker by default.

Messaging is production-critical in many systems, but the right broker, delivery guarantees, retry model, and operational ownership vary by team. Agents must treat messaging as an adapter extension, not as default platform code.

## When to Add Messaging

Add messaging only when the service has a concrete asynchronous workflow, such as:

- publishing domain events after a committed state change
- consuming commands from another service
- processing work that should not happen in the HTTP or gRPC request path
- integrating with a platform event bus that already exists

Do not add messaging just because most production systems eventually need it.

## Broker Choice

Do not choose a broker casually.

Use the dependency decision template in `dependency-policy.md` and document:

- broker or cloud service name
- client library
- delivery semantics
- ordering assumptions
- retry behavior
- dead-letter behavior
- local development strategy
- integration test strategy

Reasonable choices can include NATS, Kafka, RabbitMQ, SQS, Pub/Sub, or another platform-owned service. The template should not imply one universal answer.

## Package Boundary

If messaging is added, create a package such as:

```txt
com.example.service
└── messaging/
```

The `messaging` package may contain:

- message listeners
- publisher adapters
- broker lifecycle wiring
- message DTOs or serializers
- retry/idempotency adapter glue

The `messaging` package must not contain business rules.

Handlers should translate broker messages and call `application` services. Application services should not depend on broker client APIs.

## Publishing

Prefer publishing from application outcomes, not from HTTP or gRPC handlers.

For database-backed workflows, consider the transactional outbox pattern when a message must reflect committed database state. Do not publish directly before a transaction commits.

Before adding a publisher, define:

- event name and version
- payload schema
- partitioning or subject strategy
- idempotency key
- failure behavior
- observability fields

## Consuming

Consumers must be safe to retry.

Before adding a consumer, define:

- acknowledgement timing
- retry policy
- poison-message handling
- dead-letter or parking-lot flow
- idempotency storage
- concurrency limits
- shutdown behavior

Do not acknowledge a message before durable work is complete unless the workflow is explicitly best-effort.

## Message Contracts

Keep contracts small and versioned.

Prefer explicit schemas when the broker ecosystem supports them. If using JSON, keep DTOs separate from domain records. If using protobuf, place contracts under `app/src/main/proto` and keep generated sources out of version control.

Never expose persistence records or jOOQ types in message payloads.

## Testing

Add fast unit tests for message mapping and application orchestration.

Add integration tests when broker semantics matter, especially:

- acknowledgement behavior
- retry behavior
- serialization compatibility
- subject or topic names
- outbox publishing

Use Testcontainers when the broker supports it and the test remains reliable in CI.

## Observability

Messaging adapters should log:

- message type
- message id or idempotency key
- correlation id
- retry attempt when available
- failure reason

Do not log secrets or full sensitive payloads.

Propagate correlation ids from message metadata when available. Generate one when absent.

## What Agents Must Not Do

Agents must not:

- add a broker client as default template infrastructure
- add background workers without graceful shutdown
- bury business logic in message handlers
- acknowledge before durable success without documenting why
- add retry loops that can duplicate side effects
- invent a fake event-driven domain
- add schema registry, stream processing, or platform tooling without a concrete need
