FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY common-lib/pom.xml common-lib/pom.xml
COPY event-streaming-service/pom.xml event-streaming-service/pom.xml

RUN sed -i '/<modules>/,/<\/modules>/{ /<module>/{ /common-lib/!{ /event-streaming-service/!d } } }' pom.xml

RUN mvn -B -ntp -pl event-streaming-service -am dependency:go-offline

COPY common-lib/src common-lib/src
COPY event-streaming-service/src event-streaming-service/src

RUN mvn -B -ntp -pl event-streaming-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/event-streaming-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
