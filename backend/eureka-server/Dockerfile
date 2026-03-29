FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml

RUN mvn -B -ntp -pl eureka-server -am dependency:go-offline

COPY eureka-server/src eureka-server/src

RUN mvn -B -ntp -pl eureka-server -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/eureka-server/target/*SNAPSHOT.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
