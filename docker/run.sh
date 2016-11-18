#!/usr/bin/env bash
docker run --rm -it -v `pwd`/../:/usr/src/fileserver -v maven:/root/.m2 -w /usr/src/fileserver maven:3-jdk-8 mvn package && docker-compose up
