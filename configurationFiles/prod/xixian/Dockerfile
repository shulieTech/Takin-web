FROM  harbor.dcos.xixian.unicom.local/skyeye-platform-xx-pbc/stresstest/base:1.0.0
WORKDIR /data/apps/takin-web
COPY takin-web-app/target/takin-web-5.10.2.2.jar ./takin-web.jar
COPY repositories/ ./plugins/
COPY configurationFiles/prod/xixian/properties/msyh.ttf ./config/fonts/msyh.ttf
COPY configurationFiles/prod/xixian/properties/application.properties ./config/application.properties
COPY configurationFiles/common/pinpoint-agent-2.0.3-origin  ./pinpoint-agent-2.0.3-origin
COPY configurationFiles/prod/xixian/start.sh ./start.sh
RUN chmod +x ./start.sh