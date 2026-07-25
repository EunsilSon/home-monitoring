FROM eclipse-temurin:26-jre

WORKDIR /app

COPY app.jar ./app.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]
