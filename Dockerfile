# ---- Build stage: compile the application JAR inside the image ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace/app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage: run the previously built JAR ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app/target/onlinebank-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=docker"]
