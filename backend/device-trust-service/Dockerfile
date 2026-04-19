FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY common-lib/pom.xml common-lib/pom.xml
COPY device-trust-service/pom.xml device-trust-service/pom.xml

RUN sed -i '/<modules>/,/<\/modules>/{ /<module>/{ /common-lib/!{ /device-trust-service/!d } } }' pom.xml

RUN mvn -B -ntp -pl device-trust-service -am dependency:go-offline

COPY common-lib/src common-lib/src
COPY device-trust-service/src device-trust-service/src

RUN mvn -B -ntp -pl device-trust-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/device-trust-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
