FROM 10.172.49.246/skyeye-platform/stresstest/base:1.0.0
WORKDIR /data/apps/takin-web
COPY takin-web-app/target/takin-web-5.10.2.2.jar /data/apps/takin-web/takin-web.jar
COPY /root/repositories/* /data/apps/takin-web/plugins/
COPY doc/test/msyh.ttf /data/apps/takin-web/config/fonts/msyh.ttf
COPY doc/test/dept_example.csv /data/apps/takin-web/config/file/example/
COPY doc/test/example.csv /data/apps/takin-web/config/file/example/
COPY doc/test/application.properties /data/apps/takin-web/config/application.properties