# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [RELEASED]

## [1.0.0]

### Added

- `GET /api/v1/prices` REST endpoint (OpenAPI 3.0.3 contract-first) to query applicable prices by `brandId`, `productId`, and `applicationDate`.
- Hexagonal architecture domain model.
- H2 persistence adapter with Flyway schema and sample dataset migrations.
- RFC 9457 `ProblemDetail` error handling and `X-Correlation-Id` request tracing.
- Automated test suite (JUnit 6 unit/slice/integration tests, Karate v2 BDD, and JaCoCo coverage).
- Docker runtime (`Dockerfile`, `docker-compose.yml`), Postman collection, and project documentation.
