FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY common-lib/pom.xml common-lib/pom.xml
COPY api-gateway-service/pom.xml api-gateway-service/pom.xml

RUN sed -i '/<modules>/,/<\/modules>/{ /<module>/{ /common-lib/!{ /api-gateway-service/!d } } }' pom.xml

RUN mvn -B -ntp -pl api-gateway-service -am dependency:go-offline

COPY common-lib/src common-lib/src
COPY api-gateway-service/src api-gateway-service/src

RUN mvn -B -ntp -pl api-gateway-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/api-gateway-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
