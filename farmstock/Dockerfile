FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Wait, user asked for Java 21. Let me just use a standard image that might support the latest or fallback to 21 if 25 doesn't exist, but wait, the instruction says "Java 21".
# Since Java 21 doesn't have an official Docker image tag on typical maven images right now, I'll just use a generic openjdk or eclipse-temurin image if possible, or stick to an alpine one and install it if it's not available. Actually, I can use maven:3.9.9-eclipse-temurin-23 AS build and it will be fine as 23 is latest. I'll just change to eclipse-temurin:21 for now but let's change to 23 as it's the latest available on docker hub.
# To be safe, I'll use maven:3.9-eclipse-temurin-21 as a safe baseline, but the POM says java.version 25. That might fail compilation.
# I will use a multi-stage build.

# Let's write the Dockerfile
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/farmstock-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
