#!/bin/bash

#JAVA_OPTS=\
#"-javaagent:/data/apps/takin-amdb/pinpoint-agent-2.0.3-origin/pinpoint-bootstrap.jar \
#-Dpinpoint.agentId=${HOSTNAME:0-16} \
#-Dpinpoint.applicationName=stresstest_cloud_xixian_test \
#-Dpinpoint.licence=1d07fa023d02d60d \
#-Duser.timezone=Asia/Shanghai"
dir=$PWD
hostName=${HOSTNAME:0:16}

JAVA_OPTS=\
"-javaagent:$dir/pinpoint-agent-2.0.3-origin/pinpoint-bootstrap.jar \
-Dpinpoint.agentId=$hostName \
-Dpinpoint.applicationName=pressure_web_xixian_prod \
-Dpinpoint.licence=4AHVKh0zZIp3DLkE \
-Dpinpoint.log=$dir/pinpoint-agent-2.0.3-origin/ \
-Duser.timezone=Asia/Shanghai \
-Xms3g \
-Xmx3g"

java ${JAVA_OPTS} -jar ./takin-web.jar