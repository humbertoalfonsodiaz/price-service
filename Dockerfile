# Multi-stage Dockerfile for Price Service

# ------------------------------------------------------------------------------
# Build Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and POM for dependency layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw

# Copy source code and build executable JAR
COPY src src
RUN ./mvnw clean package -DskipTests

# ------------------------------------------------------------------------------
# Runtime Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS runtime

# Install curl for container health checks
RUN apk add --no-cache curl && \
    addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Run as non-privileged user
USER spring:spring

COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
