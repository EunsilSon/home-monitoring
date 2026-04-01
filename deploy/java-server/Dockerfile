FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY homemonitoring-*.jar ./app.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]
