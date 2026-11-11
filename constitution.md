# Project Constitution: Inditex E-Commerce Price Service

This document establishes the foundational engineering principles, architectural boundaries, and non-negotiable quality gates governing the **Inditex E-Commerce Price Service**. All contributors and automated agents must adhere strictly to these tenets when designing, implementing, testing, or refactoring code in this repository.

---

## I. Architectural Principles: Hexagonal Architecture (Ports & Adapters)

The codebase is structured around **Hexagonal Architecture** with strict inward-pointing dependencies:

```
infrastructure  ──►  application  ──►  domain
```

### 1. Domain Layer (`com.inditex.ecommerce.priceservice.domain`)
- **Framework Independence:** The `domain` package (`model`, `service`, `exception`) must remain 100% pure Java. It must **never** import Spring Framework (`org.springframework.*`), Jakarta EE / JPA (`jakarta.*`), Jackson, or any infrastructure/library annotations.
- **Zero Outward Dependencies:** The domain layer has no knowledge of databases, HTTP, REST controllers, OpenAPI DTOs, or configuration wiring.

### 2. Application Layer (`com.inditex.ecommerce.priceservice.application`)
- **Use-Case Orchestration:** Coordinates domain objects and ports (`port.in` for driving use cases and queries, `port.out` for driven infrastructure contracts).
- **Framework Agnosticism:** Application services (`GetApplicablePriceService`) must not be annotated with `@Service` or `@Component`. They are plain Java classes instantiated and wired explicitly in the infrastructure configuration (`BeanConfiguration`). Only `org.slf4j` is permitted for logging.

### 3. Infrastructure Layer (`com.inditex.ecommerce.priceservice.infrastructure`)
- **Driving Adapters (`adapter.in.web`):** REST controllers, web mappers, servlet filters (`CorrelationIdFilter`), and global exception handlers (`GlobalExceptionHandler`).
- **Driven Adapters (`adapter.out.persistence`):** JPA repositories, JPA entities (`PriceJpaEntity`), persistence mappers, and port adapter implementations (`PriceJpaAdapter`).
- **Configuration (`config`):** Explicit Spring `@Configuration` classes (`BeanConfiguration`, `OpenApiConfiguration`) that wire domain and application beans into the Spring context.

---

## II. Tactical Domain-Driven Design (DDD)

1. **Rich, Self-Validating Value Objects:**
   - Primitive obsession is prohibited in the domain and application layers. Domain concepts must be modeled as immutable Java `record` Value Objects (`BrandId`, `ProductId`, `PriceListId`, `Priority`, `DateRange`, `Money`).
   - Value Objects and Queries (`GetApplicablePriceQuery`) must enforce their own invariants inside compact constructors immediately upon instantiation, throwing `DomainValidationException` when violated.
2. **Encapsulated Entities & Aggregates:**
   - Domain entities (`Price`) are immutable, enforce non-null invariants in their constructors, and expose business behavior (`isApplicableAt(LocalDateTime)`, `hasHigherPriorityThan(Price)`) rather than anemic setters.
3. **Domain Services for Cross-Entity Logic:**
   - Business rules that operate across multiple domain objects—such as tariff priority disambiguation (`PriceDisambiguator`)—reside in stateless domain services.
4. **Monetary Precision:**
   - All monetary values (`Money`) must use `BigDecimal` scaled to 2 decimal places with `RoundingMode.HALF_UP` and validate currency codes against ISO 4217 (`java.util.Currency`).

---

## III. API-First (Contract-First) Governance

1. **Single Source of Truth:**
   - `src/main/resources/static/openapi.yml` (OpenAPI 3.0.3) is the authoritative contract for all REST endpoints, parameters, schemas, and HTTP error responses.
2. **Compile-Time Code Generation:**
   - `openapi-generator-maven-plugin` generates the Spring MVC interface (`PricesApi`) and wire DTOs (`PriceResponse`, `ProblemDetail`) during `generate-sources`.
   - Controllers (`PriceController`) **must** implement the generated API interfaces rather than declaring ad-hoc `@RequestMapping` or `@GetMapping` signatures.
   - Never manually create or edit classes under `target/generated-sources/openapi`. Modify `openapi.yml` first and regenerate.

---

## IV. Zero Annotation Processors & Explicit Code

1. **No Lombok, No MapStruct:**
   - Bytecode manipulation and annotation processors for boilerplate generation or mapping are strictly forbidden.
2. **Explicit Pure-Java Mappers:**
   - Translation between layers must be performed by explicit, stateless, null-safe mapper classes (`PricePersistenceMapper`, `PriceWebMapper`) with 100% deterministic unit/slice testability and full stack-trace transparency.
3. **Constructor Injection Only:**
   - Field injection (`@Autowired` on fields) and setter injection are forbidden. All dependencies must be declared as `private final` fields and injected via explicit constructors.

---

## V. Error Handling & Observability Standards

1. **RFC 9457 Problem Details:**
   - Every HTTP error response (`400 Bad Request`, `404 Not Found`, `500 Internal Server Error`) must be formatted as an `application/problem+json` (`org.springframework.http.ProblemDetail`) payload via `GlobalExceptionHandler`.
   - Every `ProblemDetail` must be enriched with `timestamp` (ISO-8601 UTC `Instant`) and `correlationId`.
2. **End-to-End Request Tracing:**
   - `CorrelationIdFilter` guarantees every request carries an `X-Correlation-Id` (reusing the inbound header or generating a UUID), binds it to SLF4J `MDC` for structured log correlation, echoes it in the HTTP response header, and cleans up the `MDC` in a `finally` block.

---

## VI. Database & Persistence Rules

1. **Flyway-Managed Evolution:**
   - Schema DDL (`V1__create_prices_table.sql`) and seed data (`V2__insert_sample_prices.sql`) are strictly controlled by Flyway under `src/main/resources/db/migration/`.
   - Hibernate `ddl-auto` must remain `validate` across all environments to guarantee schema fidelity.
2. **Separation of Persistence & Domain Models:**
   - JPA entities (`PriceJpaEntity`) belong exclusively to `infrastructure.adapter.out.persistence.entity` and must never leak into `application` or `domain` packages.
3. **Indexed Query Performance:**
   - Queries resolving tariffs by `brand_id`, `product_id`, date range, and `priority DESC` must align with the composite index `idx_prices_lookup`.

---

## VII. Multi-Tier Testing & Quality Gates

No change is complete unless verified across the full testing pyramid via `./mvnw clean verify`:

1. **Domain & Application Unit Tests:**
   - Fast, isolated JUnit 6 + AssertJ + Mockito tests (`MoneyTest`, `DateRangeTest`, `PriceTest`, `PriceDisambiguatorTest`, `GetApplicablePriceServiceTest`) verifying business rules, boundary dates, and invariant failures without starting a Spring context.
2. **Infrastructure Slice Tests:**
   - `@WebMvcTest` (`PriceControllerWebMvcTest`) for web routing, parameter validation, and RFC 9457 error serialization.
   - `@DataJpaTest` (`PriceJpaAdapterDataJpaTest`) for JPA queries, Flyway migrations, and persistence mapping.
3. **Full-Stack Integration & BDD Acceptance Tests:**
   - `@SpringBootTest` + `@AutoConfigureMockMvc` (`PriceQueryIntegrationTest`, `OpenApiDocumentationTest`) testing all 5 official Inditex tariff resolution scenarios and OpenAPI endpoints.
   - **Karate v2 BDD (`PricesKarateIntegrationTest` / `prices.feature`):** Black-box HTTP contract verification over a live random port.
4. **Code Coverage:**
   - JaCoCo coverage reports are generated automatically during the `verify` phase (`target/site/jacoco/index.html`).

---

## VIII. Containerization & Runtime Security

- **Immutable Multi-Stage Builds:** Built with `eclipse-temurin:25-jdk-alpine` and executed on minimal `eclipse-temurin:25-jre-alpine`.
- **Least Privilege Execution:** The container runtime must execute as a non-root user (`spring:spring`), configure container-aware JVM memory ergonomics (`-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`), and expose `/actuator/health` for container health checks.
