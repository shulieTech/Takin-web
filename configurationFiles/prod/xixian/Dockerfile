FROM harbor.dcos.xixian.unicom.local/skyeye-platform-xx-pbc/stresstest/base:1.0.0
WORKDIR /data/apps/takin-web
COPY takin-web-app/target/takin-web-5.10.2.2.jar /data/apps/takin-web/takin-web.jar
COPY repositories/ /data/apps/takin-web/plugins/
COPY configurationFiles/prod/xixian/properties/msyh.ttf /data/apps/takin-web/config/fonts/msyh.ttf
COPY configurationFiles/prod/xixian/properties/dept_example.csv /data/apps/takin-web/config/file/example/
COPY configurationFiles/prod/xixian/properties/example.csv /data/apps/takin-web/config/file/example/
COPY configurationFiles/prod/xixian/properties/application.properties /data/apps/takin-web/config/application.properties
COPY configurationFiles/prod/xixian/properties/kafka.properties /data/apps/takin-web/config/kafka.properties
