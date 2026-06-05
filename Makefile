SHELL := /bin/sh

.DEFAULT_GOAL := help

.PHONY: help format format-check lint test integration-test verify clean run dev smoke docker-build postgres-up postgres-down

help:
	@printf '%s\n' 'Common development targets:'
	@printf '  %-18s %s\n' 'make format' 'Apply Google Java Format with Spotless'
	@printf '  %-18s %s\n' 'make format-check' 'Check Google Java Format without rewriting files'
	@printf '  %-18s %s\n' 'make lint' 'Run Google Checkstyle'
	@printf '  %-18s %s\n' 'make test' 'Run fast unit and HTTP tests'
	@printf '  %-18s %s\n' 'make integration-test' 'Run Testcontainers PostgreSQL integration tests'
	@printf '  %-18s %s\n' 'make verify' 'Run the full build, tests, formatting, and linting'
	@printf '  %-18s %s\n' 'make run' 'Start the service locally'
	@printf '  %-18s %s\n' 'make dev' 'Start PostgreSQL, then start the service'
	@printf '  %-18s %s\n' 'make smoke' 'Call local health and readiness endpoints'
	@printf '  %-18s %s\n' 'make docker-build' 'Build the service container image'
	@printf '  %-18s %s\n' 'make postgres-up' 'Start local PostgreSQL'
	@printf '  %-18s %s\n' 'make postgres-down' 'Stop local PostgreSQL'
	@printf '  %-18s %s\n' 'make clean' 'Remove Maven build output'

format:
	./mvnw spotless:apply

format-check:
	./mvnw spotless:check

lint:
	./mvnw checkstyle:check -DskipTests -DskipITs

test:
	./mvnw test

integration-test:
	./mvnw -DskipTests failsafe:integration-test failsafe:verify

verify:
	./mvnw clean verify

clean:
	./mvnw clean

run:
	./mvnw -pl app exec:java

dev: postgres-up run

smoke:
	curl --fail --silent --show-error http://localhost:8080/health
	curl --fail --silent --show-error http://localhost:8080/ready

docker-build:
	docker build -t service-template:local .

postgres-up:
	docker compose up -d postgres

postgres-down:
	docker compose down
