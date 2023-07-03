FROM swr.cn-east-3.myhuaweicloud.com/shulie-hangzhou/openjdk:8-jdk-alpine
WORKDIR /data/takin-web
RUN cd / && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN apk --no-cache add fontconfig ttf-dejavu
ADD https://install-pkg.oss-cn-hangzhou.aliyuncs.com/takin-web/config.zip /data/takin-web
COPY takin-web*.jar  /data/takin-web/takin-web.jar
COPY plugins /data/takin-web/plugins
ENTRYPOINT ["java","-jar","takin-web.jar"]