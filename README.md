# Inditex E-Commerce Price Service

Production-grade Spring Boot microservice implementing **Hexagonal Architecture (Ports and Adapters)** with **Tactical Domain-Driven Design (DDD)** and an **API-First (Contract-First)** workflow.

The service provides a REST endpoint to query applicable product commercial prices and tariffs based on brand, product identifier, and application date, applying priority disambiguation rules whenever multiple tariffs overlap in time.

---

## Technical Stack & Architecture

- **Runtime & Language:** Java 25 LTS (utilizing immutable `record`s for Value Objects and pure domain encapsulation)
- **Framework:** Spring Boot 4.1.2 (Spring Framework 7, Jakarta EE 11)
- **Architecture:** Hexagonal Architecture (Ports & Adapters) with Tactical DDD
- **API-First Specification:** OpenAPI 3.0.3 (`src/main/resources/static/openapi.yml`) generating compile-time Spring MVC contracts via `openapi-generator-maven-plugin:7.27.0`
- **Interactive Documentation:** SpringDoc OpenAPI 3.2.0 (`/swagger-ui.html`, `/api-docs`)
- **Zero Annotation Processors:** No Lombok and no MapStruct; utilizes explicit, stateless pure-Java mappers (`PricePersistenceMapper`, `PriceWebMapper`) for full transparency and debuggability
- **Database & Migrations:** In-memory H2 database with Flyway schema (`V1`) and sample dataset (`V2`) migrations
- **Testing:** JUnit 6 (Jupiter), Mockito, AssertJ, and **Karate v2 BDD** (`io.karatelabs:karate-junit6:2.1.4`)
- **Observability:** Centralized RFC 9457 `ProblemDetail` error handling, SLF4J MDC request tracing (`X-Correlation-Id`), and Spring Boot Actuator (`/actuator/health`)
- **Containerization:** Multi-stage Dockerfile (Alpine JDK 25 builder, Alpine JRE 25 non-root runtime) and Docker Compose

---

## Getting Started

### 1. Run with Docker Compose

Build and launch the service in an isolated Alpine container:

```bash
docker compose up --build
```

The service will start on port `8080`.

To stop the container:
```bash
docker compose down
```

### 2. Run Locally with Maven Wrapper

Ensure Java 25 is installed:

```bash
./mvnw clean spring-boot:run
```

---

## API Documentation endpoints:

With the application running:

| Resource | URL | Description |
| :--- | :--- | :--- |
| **Interactive Swagger UI** | `http://localhost:8080/swagger-ui.html` | Test requests interactively with pre-filled examples |
| **OpenAPI 3 YAML Spec** | `http://localhost:8080/openapi.yml` | Raw OpenAPI 3.0.3 contract file |
| **OpenAPI 3 JSON Spec** | `http://localhost:8080/api-docs` | Dynamic JSON OpenAPI specification |
| **Actuator Health** | `http://localhost:8080/actuator/health` | Service health status and readiness probes |

---

## Running the Automated Test Suite

### How to Run the Tests

1. **Full Test Suite + Code Coverage Report:**
   Executes all unit tests, slice tests, Spring Boot integration tests, and Karate v2 BDD feature scenarios, followed by JaCoCo coverage report generation:
   ```bash
   ./mvnw clean verify
   ```

2. **Run Tests Only (Skip Packaging & Verification Phase):**
   ```bash
   ./mvnw test
   ```

3. **Run a Specific Test Class or Layer:**
   - **Karate BDD Suite only:**
     ```bash
     ./mvnw test -Dtest=PricesKarateIntegrationTest
     ```
   - **Spring Boot MockMvc Integration Tests only:**
     ```bash
     ./mvnw test -Dtest=PriceQueryIntegrationTest
     ```
   - **Domain & Application Unit Tests only:**
     ```bash
     ./mvnw test -Dtest="*Test,!*IntegrationTest,!*WebMvcTest,!*DataJpaTest"
     ```

### Test Results & Reports

After running `./mvnw clean verify`, test execution outputs and reports are generated under the `target/` directory:

| Report Type | Format | Location |
| :--- | :--- | :--- |
| **Maven Surefire (JUnit 6)** | TXT & JUnit XML | `target/surefire-reports/` (`*.txt`, `TEST-*.xml`) |
| **Karate v2 BDD Summary** | Interactive HTML | `target/karate-reports/karate-summary.html` (or `target/karate-reports/index.html`) |
| **Karate Feature Details** | HTML & Timeline | `target/karate-reports/feature-html/karate.prices.html` & `target/karate-reports/karate-timeline.html` |
| **JaCoCo Code Coverage** | HTML, XML, CSV | `target/site/jacoco/index.html` (`jacoco.xml`, `jacoco.csv`, and raw `target/jacoco.exec`) |

---

## Postman Collection

Import `postman/inditex-price-service.postman_collection.json` into Postman to run all 5 official test cases and edge cases with automated assertion scripts.
