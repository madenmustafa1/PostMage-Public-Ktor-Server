FROM --platform=$BUILDPLATFORM gradle:7-jdk11 AS build
ARG TARGETPLATFORM
ARG BUILDPLATFORM
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/com.maden.postmage-server-0.0.1.jar
COPY src/main/kotlin/com/postmage/util/strings/en.json /app/com/postmage/util/strings/en.json
COPY src/main/kotlin/com/postmage/util/strings/tr.json /app/com/postmage/util/strings/tr.json
COPY /web /app/web

COPY fcm-service-account.json fcm-service-account.json
ENTRYPOINT ["java","-jar","/app/com.maden.postmage-server-0.0.1.jar"]