# Contributing

Thanks for improving this template. Keep changes small, explicit, and useful for many lightweight Java services.

## Development Workflow

1. Read `AGENTS.md`.
2. Read the relevant guide in `.agents/`.
3. Make the smallest change that solves the problem.
4. Run `make format`.
5. Run `make verify`.
6. Update README and `.agents/` docs when structure, commands, dependencies, or rules change.

## Commit Style

Use Conventional Commits:

```txt
feat: add service template capability
fix: correct readiness behavior
build: update maven plugin configuration
docs: clarify template customization
test: cover configuration parsing
```

## Dependency Changes

Do not add dependencies casually. Use the dependency decision template in `.agents/dependency-policy.md` when adding anything new.

## Pull Requests

Pull requests should explain:

- what changed
- why it belongs in a lightweight template
- what was intentionally not added
- which checks were run
