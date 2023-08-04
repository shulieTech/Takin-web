#!/bin/bash

dir=$PWD
hostName=${HOSTNAME:0:16}

JAVA_OPTS=\
"-Dpinpoint.agentId=$hostName \
-Dpinpoint.applicationName=pressure_web_xixian_test \
-Dpinpoint.licence=1d07fa023d02d60d \
-Duser.timezone=Asia/Shanghai \
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
-Xms3g \
-Xmx3g"

java ${JAVA_OPTS} -jar ./takin-web.jar