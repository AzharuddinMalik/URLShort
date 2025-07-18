# Stage 1: Build the Spring Boot application using a Maven image
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven pom.xml file to leverage Docker caching
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the application
# -DskipTests skips running tests during the build, which is common for deployment builds
# --batch-mode prevents interactive prompts
RUN mvn clean install -DskipTests --batch-mode

# Stage 2: Create the final, smaller runtime image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
# The JAR name is typically artifactId-version.jar (e.g., url-shortener-0.0.1-SNAPSHOT.jar)
# Adjust the name below if yours is different
COPY --from=build /app/target/url-shortener-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application runs on (default is 8080)
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
