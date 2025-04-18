FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . /app
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-jammy AS builder
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar ./${SERVICE_NAME}.jar
RUN java -Dlog4j.configurationFile=log4j2-build.xml -Djarmode=layertools -jar ${SERVICE_NAME}.jar extract

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]