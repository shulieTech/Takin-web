FROM 10.172.49.246/skyeye-platform/stresstest/base:1.0.0
WORKDIR /data/apps/takin-web
COPY takin-web-app/target/takin-web-5.10.2.2.jar /data/apps/takin-web/takin-web.jar
COPY repositories/ /data/apps/takin-web/plugins/
COPY configurationFiles/test/xixian/properties/msyh.ttf /data/apps/takin-web/config/fonts/msyh.ttf
COPY configurationFiles/test/xixian/properties/dept_example.csv /data/apps/takin-web/config/file/example/
COPY configurationFiles/test/xixian/properties/example.csv /data/apps/takin-web/config/file/example/
COPY configurationFiles/test/xixian/properties/application.properties /data/apps/takin-web/config/application.properties
