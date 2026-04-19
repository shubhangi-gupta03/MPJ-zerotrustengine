FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY common-lib/pom.xml common-lib/pom.xml
COPY policy-engine-service/pom.xml policy-engine-service/pom.xml

RUN sed -i '/<modules>/,/<\/modules>/{ /<module>/{ /common-lib/!{ /policy-engine-service/!d } } }' pom.xml

RUN mvn -B -ntp -pl policy-engine-service -am dependency:go-offline

COPY common-lib/src common-lib/src
COPY policy-engine-service/src policy-engine-service/src

RUN mvn -B -ntp -pl policy-engine-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/policy-engine-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
