# 베이스 이미지: JDK 포함
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (docker-compose.yml의 context 기준 상대 경로)
COPY homemonitoring-*.jar ./app.jar

# 프로덕션 프로필 환경 변수
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
