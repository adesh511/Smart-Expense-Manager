FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project metadata first for better layer caching.
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Copy source and build the jar.
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
