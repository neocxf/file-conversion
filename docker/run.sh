#!/usr/bin/env bash
# 1. if there are already running or stopped container services, we should stop it
echo " removing the old containers ..."
docker-compose down > /dev/null 2>&1 

# 2. we inspect the system's maven local repository, and create an container which map the host's repository to the container's volume for later use
docker create -v `mvn help:evaluate -Dexpression=settings.localRepository | grep -v '\[INFO\]'`:/root/.m2 --name=maven busybox:1.25 /bin/true > /dev/null 2>&1

# 3. build the src files and start the docker-compose 
echo " going to compile all the sources, and start up the containers ..."
docker run --rm -it --net=bridge --volumes-from maven -v `pwd`/../:/usr/src/fileserver -w /usr/src/fileserver maven:3-jdk-8 mvn package && docker-compose up
