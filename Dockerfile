# 1️⃣ 빌드 단계 (Gradle 빌드)
FROM gradle:7.6-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .
RUN ./gradlew clean build -x test

# 2️⃣ 실행 단계
FROM openjdk:17
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/Demotion-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
