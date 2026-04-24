FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/stats-dto-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]