FROM gradle:8.9.0-jdk17 AS builder
WORKDIR /workspace
COPY gradle ./gradle
COPY gradlew settings.gradle* build.gradle* ./
RUN chmod +x gradlew
RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*
RUN ./gradlew --version --no-daemon
COPY . .
RUN ./gradlew clean bootJar -x test --no-daemon --stacktrace --info

FROM eclipse-temurin:21-jre
ENV TZ=Asia/Seoul
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=prod","-jar","/app/app.jar"]