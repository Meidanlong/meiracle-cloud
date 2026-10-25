FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/meiracle-monitor.jar app.jar
EXPOSE 9211
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
