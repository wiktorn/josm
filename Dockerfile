FROM openjdk:8-jdk-alpine

RUN apk update && \
    apk add apache-ant && \
    rm /var/cache/apk/*

COPY . /josm

CMD cd /josm && \
    ant test
