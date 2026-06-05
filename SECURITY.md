# Security Policy

## Supported Versions

This repository is a template. Security fixes should be applied to the current `main` branch and then pulled into services created from the template as appropriate.

## Reporting a Vulnerability

Please report security issues privately to the repository owner instead of opening a public issue.

Include:

- affected dependency or code path
- impact
- reproduction steps when safe
- suggested fix if known

## Template Security Notes

- Do not commit secrets.
- Use environment variables or a deployment secret manager.
- Treat `.env.example` as documentation only.
- Add authentication, authorization, and network policy in the consuming service or platform, not in this baseline template.
