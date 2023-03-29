FROM alpine:3.17.2
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories \
    && apk update \
    && apk add openjdk8 unzip zip vim curl tzdata \
    && apk add bash nfs-utils \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo Asia/Shanghai > /etc/timezone \
    && apk del tzdata \
    && rm -rf /tmp/* /var/cache/apk/*