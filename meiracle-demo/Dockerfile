FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/meiracle-demo.jar app.jar
EXPOSE 9201
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
