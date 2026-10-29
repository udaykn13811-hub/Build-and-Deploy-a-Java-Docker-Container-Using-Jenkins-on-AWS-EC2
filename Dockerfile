
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/java-docker-demo.jar app.jar

ENV PORT=8081

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

