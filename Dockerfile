FROM gradle:8.9.0-jdk21 AS builder
WORKDIR /workspace
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew --version
COPY . .
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
ENV TZ=Asia/Seoul
WORKDIR /app
COPY --from=builder /workspace/build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=prod","-jar","/app/app.jar"]
