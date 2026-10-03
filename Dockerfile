# syntax = docker/dockerfile:1.2
FROM clojure:temurin-25-tools-deps AS build

WORKDIR /
COPY . /

RUN clj -Sforce -T:build all

FROM azul/zulu-openjdk-alpine:25-jre-headless

COPY --from=build /target/feed-the-comments-standalone.jar /feed-the-comments/feed-the-comments-standalone.jar

EXPOSE $PORT

ENTRYPOINT exec java $JAVA_OPTS -jar /feed-the-comments/feed-the-comments-standalone.jar
