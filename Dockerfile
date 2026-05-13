ARG SERVICE_NAME

FROM maven:3.9-eclipse-temurin-26 AS build
ARG SERVICE_NAME
WORKDIR /workspace

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY ai-services/pom.xml ai-services/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY discovery-client/pom.xml discovery-client/pom.xml
COPY payment-services/pom.xml payment-services/pom.xml
COPY notification-services/pom.xml notification-services/pom.xml
COPY security-services/pom.xml security-services/pom.xml

RUN mvn -B -q -pl ${SERVICE_NAME} -am dependency:go-offline

COPY . .
RUN mvn -B -q -pl ${SERVICE_NAME} -am package -DskipTests

FROM eclipse-temurin:26-jre-ubi10-minimal
ARG SERVICE_NAME
WORKDIR /app

COPY --from=build /workspace/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
