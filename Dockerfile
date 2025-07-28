# # -------- Stage 1: Build --------
# FROM eclipse-temurin:17-jdk AS builder

# WORKDIR /app

# # Copy the Spring Boot JAR from the target folder
# COPY target/springboot-backend-0.0.1-SNAPSHOT.jar app.jar

# # -------- Stage 2: Runtime --------
# FROM gcr.io/distroless/java17-debian11

# # Copy only the built JAR to the minimal runtime image
# COPY --from=builder /app/app.jar /app.jar

# # Start the app
# ENTRYPOINT ["java", "-jar", "/app.jar"]


# -------- Stage 1: Build --------
    FROM eclipse-temurin:17-jdk AS builder

    WORKDIR /app
    
    # Copy Maven configuration and source code
    COPY pom.xml .
    COPY src ./src
    
    # Install Maven and build the project
    RUN apt-get update && \
        apt-get install -y maven && \
        mvn clean package -DskipTests
    
    # -------- Stage 2: Runtime --------
    FROM eclipse-temurin:17-jdk-alpine
    
    WORKDIR /app
    
    # Copy the built jar from the builder stage
    COPY --from=builder /app/target/*.jar app.jar
    
    # Set the port to match your Spring Boot configuration
    EXPOSE 8081
    
    # Run the application
    ENTRYPOINT ["java", "-jar", "app.jar"]
    