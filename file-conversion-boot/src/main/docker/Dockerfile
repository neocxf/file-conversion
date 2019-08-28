FROM neocxf/phantom-jre8:2.1.1

MAINTAINER "neo.chen <neocxf@gmail.com>"

WORKDIR /app

ADD dsone-fileserver-boot.jar fileserver.jar

RUN sh -c 'touch /app/fileserver.jar'

ENV JAVA_OPTS = ""

ENTRYPOINT ["java", "-D$JAVA_OPTS", "-Djava.security.egd=file:/dev/./urandom", "-jar", "fileserver.jar", "--server.port=8091"]

EXPOSE 8910 8091
