# -------- Stage 1: Build --------
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy the Spring Boot JAR from the target folder
COPY target/springboot-backend-0.0.1-SNAPSHOT.jar app.jar

# -------- Stage 2: Runtime --------
FROM gcr.io/distroless/java17-debian11

# Copy only the built JAR to the minimal runtime image
COPY --from=builder /app/app.jar /app.jar

# Start the app
ENTRYPOINT ["java", "-jar", "/app.jar"]
