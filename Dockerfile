# Estágio 1: Build com Maven e JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Estágio 2: Runtime enxuto com JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV TZ=America/Sao_Paulo
ENV PORT=8080

RUN addgroup -S ecopulse && adduser -S ecopulse -G ecopulse
COPY --from=build /app/target/ecopulse-api.jar /app/app.jar
RUN chown -R ecopulse:ecopulse /app

USER ecopulse
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
