# AGENTS.md — AI Coding Agent Guide

This file provides essential context, architectural boundaries, build/test commands, and coding conventions for AI agents operating in the **Inditex E-Commerce Price Service** repository. Read this guide and [constitution.md](constitution.md) before proposing or applying changes.

---

## 1. Project Overview & Tech Stack

- **Purpose:** REST microservice that determines the applicable commercial price and tariff (`priceList`) for a given `brandId`, `productId`, and `applicationDate`, resolving overlapping date ranges by highest `priority`.
- **Language & Runtime:** Java 25 LTS
- **Framework:** Spring Boot 4.1.2 (Spring Framework 7, Jakarta EE 11)
- **Architecture:** Hexagonal Architecture (Ports & Adapters) + Tactical DDD + API-First (OpenAPI 3.0.3)
- **Database:** H2 In-Memory (`MODE=PostgreSQL`) managed by Flyway migrations (`ddl-auto: validate`)
- **Testing Stack:** JUnit 6 (Jupiter), AssertJ, Mockito, Spring Boot Test slices (`@WebMvcTest`, `@DataJpaTest`), and Karate v2 BDD (`karate-junit6:2.1.4`)
- **Build & Coverage:** Maven Wrapper (`./mvnw`) + `openapi-generator-maven-plugin` + JaCoCo (`0.8.14`)

---

## 2. Essential Commands

Always use the Maven Wrapper `./mvnw` to ensure consistent Maven version execution:

| Task | Command |
| :--- | :--- |
| **Full build, all tests & JaCoCo report** | `./mvnw clean verify` |
| **Compile & generate OpenAPI sources** | `./mvnw clean compile` |
| **Run all tests** | `./mvnw test` |
| **Run unit tests only (fast)** | `./mvnw test -Dtest="*Test,!*IntegrationTest,!*WebMvcTest,!*DataJpaTest"` |
| **Run Web MVC slice tests** | `./mvnw test -Dtest=PriceControllerWebMvcTest` |
| **Run Data JPA slice tests** | `./mvnw test -Dtest=PriceJpaAdapterDataJpaTest` |
| **Run MockMvc integration tests** | `./mvnw test -Dtest=PriceQueryIntegrationTest,OpenApiDocumentationTest` |
| **Run Karate v2 BDD suite** | `./mvnw test -Dtest=PricesKarateIntegrationTest` |
| **Start application locally (port 8080)** | `./mvnw spring-boot:run` |
| **Run via Docker Compose** | `docker compose up --build` |

---

## 3. Repository Layout & Package Boundaries

```text
src/main/java/com/inditex/ecommerce/priceservice/
├── PriceServiceApplication.java
├── domain/                                  # PURE JAVA ONLY — No Spring, No JPA, No Jackson
│   ├── exception/                           # DomainValidationException, PriceNotFoundException
│   ├── model/                               # Price (Entity) & Value Objects (BrandId, ProductId, Money, DateRange, Priority, PriceListId)
│   └── service/                             # PriceDisambiguator (stateless domain service)
├── application/                             # USE CASE ORCHESTRATION — No Spring stereotypes (@Service/@Component)
│   ├── port/
│   │   ├── in/                              # GetApplicablePriceUseCase, GetApplicablePriceQuery
│   │   └── out/                             # PriceRepositoryPort
│   └── service/                             # GetApplicablePriceService (wired via BeanConfiguration)
└── infrastructure/                          # FRAMEWORKS, DRIVERS & ADAPTERS
    ├── adapter/
    │   ├── in/web/                          # PriceController (implements generated PricesApi)
    │   │   ├── error/                       # GlobalExceptionHandler (RFC 9457 ProblemDetail)
    │   │   ├── filter/                      # CorrelationIdFilter (X-Correlation-Id & MDC)
    │   │   └── mapper/                      # PriceWebMapper (Domain -> OpenAPI DTO)
    │   └── out/persistence/                 # PriceJpaAdapter (implements PriceRepositoryPort)
    │       ├── entity/                      # PriceJpaEntity (@Entity, table "prices")
    │       ├── mapper/                      # PricePersistenceMapper (JPA Entity <-> Domain)
    │       └── repository/                  # SpringDataPriceRepository
    └── config/                              # BeanConfiguration, OpenApiConfiguration
```

### Critical Dependency Rules
1. **`domain` must NEVER import anything from `application` or `infrastructure`**, nor any framework packages (`org.springframework.*`, `jakarta.*`).
2. **`application` must NEVER import anything from `infrastructure`**. Do not annotate `application` classes with `@Service`, `@Component`, or `@Transactional`; register beans in `infrastructure.config.BeanConfiguration`.
3. **Generated OpenAPI classes** (`PricesApi`, `PriceResponse`, etc.) live in `target/generated-sources/openapi/src/main/java/com/inditex/ecommerce/priceservice/infrastructure/adapter/in/web/{api,dto}`. Never edit generated files directly—update `src/main/resources/static/openapi.yml` and run `./mvnw generate-sources`.

---

## 4. Coding Conventions & Implementation Rules

### Domain Modeling
- **Value Objects as `record`s:** Use Java `record`s with compact constructors for Value Objects (`BrandId`, `ProductId`, `PriceListId`, `Priority`, `DateRange`, `Money`) and queries (`GetApplicablePriceQuery`).
- **Fail-Fast Validation:** Validate all domain invariants in constructors and throw `DomainValidationException` on invalid input (e.g., `null` fields, `brandId <= 0`, `productId <= 0`, `priority < 0`, `startDate.isAfter(endDate)`, negative `Money` amount, or non-ISO 4217 currency code).
- **Monetary Values:** Always scale `BigDecimal` amounts to 2 decimal places using `RoundingMode.HALF_UP` and normalize currency codes via `.trim().toUpperCase()`.

### Explicit Mapping & Dependency Injection
- **No Lombok / No MapStruct:** Write explicit constructors, getters, `equals`/`hashCode`/`toString` (on classes), and explicit mapper components (`PriceWebMapper`, `PricePersistenceMapper`).
- **Constructor Injection:** Declare all dependencies as `private final` fields injected via constructor without `@Autowired`.

### REST API & Error Handling
- **API-First Workflow:** Any endpoint or schema change starts in `src/main/resources/static/openapi.yml`.
- **RFC 9457 ProblemDetail:** Map exceptions in `GlobalExceptionHandler` to `org.springframework.http.ProblemDetail`, ensuring `title`, `detail`, `instance`, `timestamp`, and `correlationId` properties are populated.
- **Tracing:** Preserve `X-Correlation-Id` propagation through `CorrelationIdFilter` and SLF4J `MDC`.

### Database Changes
- Do not modify existing Flyway migrations (`V1__create_prices_table.sql`, `V2__insert_sample_prices.sql`) unless explicitly requested to alter baseline setup. Add new sequential versioned migrations (`V3__...sql`) for schema changes.
- Keep `spring.jpa.hibernate.ddl-auto: validate` in `application.yml` and `application-test.yml`.

---

## 5. Git & Commit Conventions (Conventional Commits)

All commit messages **must** strictly follow the [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) specification:

```text
<type>[optional scope][!]: <description>

[optional body]

[optional footer(s)]
```

- **Allowed Types:**
  - `feat`: A new feature or endpoint capability (`feat(domain): ...`, `feat(application): ...`)
  - `fix`: A bug fix (`fix(persistence): ...`)
  - `refactor`: Code change that neither fixes a bug nor adds a feature
  - `test`: Adding or updating unit, slice, integration, or Karate BDD tests
  - `docs`: Documentation or OpenAPI specification updates (`docs: ...`)
- **Allowed Scopes:** `domain`, `application`, `persistence`, `config`.
- **Formatting Rules:**
  - Use the imperative, present tense in `<description>` (e.g., `"add tariff priority disambiguation"`, not `"added"` or `"adds"`).
  - Do not capitalize the first letter of `<description>` and do not end the subject line with a period (`.`).
  - Keep the header line under 72 characters.
  - Indicate breaking changes with `!` before `:` (e.g., `feat(domain)!: change price model`) and/or a `BREAKING CHANGE:` footer.

---

## 6. Verification Checklist for Agents

Before completing any task that modifies code, configuration, or contracts:

1. **Check Architectural Purity:** Confirm no `org.springframework.*` or `jakarta.*` imports were introduced into `domain` or `application`.
2. **Verify API Contract Sync:** If `openapi.yml` was modified, ensure generated sources compile cleanly and `PriceController` + `PriceWebMapper` match the generated contract.
3. **Run the Full Verification Suite:** Execute `./mvnw clean verify` and ensure all JUnit 6 unit tests, slice tests (`@WebMvcTest`, `@DataJpaTest`), integration tests (`@SpringBootTest`), and Karate v2 BDD scenarios pass with zero errors.
4. **Follow Conventional Commits:** When creating git commits or proposing commit messages, format them strictly according to Section 5.
