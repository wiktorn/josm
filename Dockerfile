FROM openjdk:8-jdk

RUN apt-get update -y && \
	apt-get install -y ant 

COPY . /josm

RUN mkdir -p /josm/test/report

CMD cd /josm && \
    ant test-my
