FROM 10.172.49.246/skyeye-platform/stresstest/base:1.0.0
WORKDIR /data/apps/takin-web
COPY takin-web-app/target/takin-web-5.10.2.2.jar ./takin-web.jar
COPY repositories/ ./plugins/
COPY configurationFiles/test/xixian/properties/msyh.ttf ./config/fonts/msyh.ttf
COPY configurationFiles/test/xixian/properties/application.properties ./config/application.properties
COPY configurationFiles/common/pinpoint-agent-2.0.3-origin  ./pinpoint-agent-2.0.3-origin
COPY configurationFiles/test/xixian/start.sh ./start.sh
RUN chmod +x ./start.sh
