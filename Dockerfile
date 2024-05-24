FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY src/ src
COPY pom.xml ./

RUN mvn package

FROM openjdk:22

WORKDIR /app

COPY --from=builder /app/target/trade-enrichment-service-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/product.csv /app/src/main/resources/product.csv
COPY src/main/resources/trade.csv /app/src/main/resources/trade.csv

LABEL name="trade-enrichment-service"

CMD ["java", "-jar", "app.jar"]
