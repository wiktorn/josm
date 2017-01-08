FROM openjdk:8-jdk

RUN apt-get update -y && \
	apt-get install -y apache-ant 

COPY . /josm

RUN mkdir -p /josm/test/report

CMD cd /josm && \
    ant test-html
