FROM gradle:jdk8 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:8-jre-slim
ENV AWS_ACCESS_KEY_ID=xxx
ENV AWS_SECRET_ACCESS_KEY=xxx
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/EasySetMeet-1.0.jar /app/
WORKDIR /app
CMD java -jar EasySetMeet-1.0.jar