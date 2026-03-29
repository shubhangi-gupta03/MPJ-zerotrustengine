FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY common-lib/pom.xml common-lib/pom.xml
COPY risk-scoring-service/pom.xml risk-scoring-service/pom.xml

RUN mvn -B -ntp -pl risk-scoring-service -am dependency:go-offline

COPY common-lib/src common-lib/src
COPY risk-scoring-service/src risk-scoring-service/src

RUN mvn -B -ntp -pl risk-scoring-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/risk-scoring-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
