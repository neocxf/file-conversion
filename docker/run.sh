#!/usr/bin/env bash
#
#  script:
#       usage: make sure that "mvn" command works fine in your terminal.
#
#       also notice that, when running this script, the host local repository will be used as maven repository build provider
#

# Emits a string to console (poor man's log)
# with an optional log level (can be anything, will be prepended to the message)
#
# usage: msg 'msg' [level]
function msg {
    local level=${2:-"INFO"}
    echo "[$level] $1"
}


# Wraps a command and exits if it fails
#
# usage: wrap cat /tmp/foo/bar
function wrap {
    local CMD="$@"
    $@
    if [[ $? != 0 ]]; then
        msg "Failed: $CMD" "ERROR"
        exit 1
    fi
}

wrap mvn -v  #check the existence of maven command

wrap docker -v         #check the existence of docker command

wrap docker-compose -v  #check the existence of docker-compose command

# 1. if there are already running or stopped container services, we should stop it
msg "removing the old containers ..."
docker-compose down > /dev/null 2>&1


# 2. we inspect the system's maven local repository, and create an container which map the host's repository to the container's volume for later use
docker create -v `mvn help:evaluate -Dexpression=settings.localRepository | grep -v '\[INFO\]'`:/root/.m2 --name=maven busybox:1.25 /bin/true > /dev/null 2>&1

# 3. build the src files and start the docker-compose 
msg "going to compile all the sources, and start up the containers ..."
docker run --rm -it --net=host --volumes-from maven -v `pwd`/../:/usr/src/fileserver -w /usr/src/fileserver maven:3-jdk-8 mvn clean package && docker-compose up