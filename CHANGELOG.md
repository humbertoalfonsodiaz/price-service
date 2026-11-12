# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [RELEASED]

## [1.0.1] - 2026-11-12

### Changed

- Optimize `GetApplicablePriceService` to execute a single candidate tariff lookup and disambiguate overlapping prices using domain `PriceDisambiguator`, avoiding duplicate database queries on 404 misses.
- Optimize `SpringDataPriceRepository.findFirstApplicablePrice` to use `FETCH FIRST 1 ROW ONLY` at the database query level instead of loading full candidate lists into memory.
- Update `GetApplicablePriceServiceTest` to verify single repository lookup and priority disambiguation.

## [1.0.0]

### Added

- `GET /api/v1/prices` REST endpoint (OpenAPI 3.0.3 contract-first) to query applicable prices by `brandId`, `productId`, and `applicationDate`.
- Hexagonal architecture domain model.
- H2 persistence adapter with Flyway schema and sample dataset migrations.
- RFC 9457 `ProblemDetail` error handling and `X-Correlation-Id` request tracing.
- Automated test suite (JUnit 6 unit/slice/integration tests, Karate v2 BDD, and JaCoCo coverage).
- Docker runtime (`Dockerfile`, `docker-compose.yml`), Postman collection, and project documentation.
