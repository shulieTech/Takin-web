#!/bin/sh

#JAVA_OPTS=\
#"-javaagent:/data/apps/takin-amdb/pinpoint-agent-2.0.3-origin/pinpoint-bootstrap.jar \
#-Dpinpoint.agentId=${HOSTNAME:0-16} \
#-Dpinpoint.applicationName=stresstest_cloud_xixian_test \
#-Dpinpoint.licence=1d07fa023d02d60d \
#-Duser.timezone=Asia/Shanghai"

JAVA_OPTS=\
"-Duser.timezone=Asia/Shanghai \
-Xms3g \
-Xmx3g"

java ${JAVA_OPTS} -jar ./takin-web.jar