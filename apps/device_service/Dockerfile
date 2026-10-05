# Используем мультистейдж для оптимизации итогового образа
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/device-service-*.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]
