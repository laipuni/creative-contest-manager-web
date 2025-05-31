#Gradle 빌드용 이미지
FROM gradle:8.5-jdk17 AS build

#소스코드 복사
WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY src /app/src

#빌드 (bootJar 생성)
RUN gradle bootJar --no-daemon

#실제 실행 이미지
FROM eclipse-temurin:17-jdk

WORKDIR /app

#빌드된 jar 복사
COPY --from=build /app/build/libs/*.jar app.jar

#환경변수 주입 가능하도록 설정 및 profile prod로 실행
ENV SPRING_PROFILES_ACTIVE=prod

#앱 실행
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","app.jar"]
