FROM swr.cn-east-3.myhuaweicloud.com/shulie-hangzhou/openjdk:8-jdk-alpine
RUN apk update && apk add nfs-utils
WORKDIR /data/takin-web
RUN cd / && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ADD https://install-pkg.oss-cn-hangzhou.aliyuncs.com/takin-cloud/lib.zip /data/takin-cloud
COPY  takin-web-app/target/takin-web*.jar  /data/takin-web/takin-web.jar
COPY plugins /data/takin-web
ENTRYPOINT ["java","-jar","takin-web.jar"]