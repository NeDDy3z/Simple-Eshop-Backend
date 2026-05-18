FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew
RUN ./gradlew build -x test
ENTRYPOINT ["java", "-jar", "build/libs/notification-0.0.1-SNAPSHOT.jar"]
