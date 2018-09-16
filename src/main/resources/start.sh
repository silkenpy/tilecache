#!/bin/bash

HOME=`pwd`
PID_FILE="/tmp/chista-tilecache.pid"

HEAP="-Xmx7g"

CONF_FILE="-Dconfig.file=${HOME}/application.hocon"
//LOG4J_FILE="-Dlog4j2.configurationFile=${HOME}/log4j2.xml"

JAR_FILE=`ls ${HOME}/tilecache*dependencies.jar`

if [[ ! -f ${JAR_FILE} ]];then
   echo ${JAR_FILE}
   echo "Jar file does not exist"
   exit
fi

java ${CONF_FILE} ${HEAP} -jar ${JAR_FILE}

echo $! > $PID_FILE