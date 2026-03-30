# Multi-stage build for H2 Fruit API

# Stage 1: Build the application with Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven configuration first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user for security (Alpine Linux syntax)
RUN addgroup -g 1000 -S appgroup && \
    adduser -u 1000 -S appuser -G appgroup
USER appuser

# Environment variables
ENV SERVER_PORT=8081
ENV SPRING_PROFILES_ACTIVE=h2

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]