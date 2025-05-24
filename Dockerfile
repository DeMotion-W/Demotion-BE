FROM openjdk:17

# JAR 파일을 컨테이너로 복사
# - 현재 폴더 구조 기준으로 JAR 위치는 build/libs/Demotion-0.0.1-SNAPSHOT.jar
COPY build/libs/Demotion-0.0.1-SNAPSHOT.jar /app/app.jar

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
