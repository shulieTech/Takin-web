package com.pamirs.takin.common.enums.ds;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: 南风
 * @Date: 2021/8/31 10:15 上午
 */
@Getter
public enum MiddleWareTypeEnum {

        HTTP_CLIENT(9,"http-client"),
        RPC(8,"RPC框架"),
        DB(2,"存储客户端"),
        LINK_POOL(0,"连接池"),
        CACHE(1,"缓存"),
        MQ(3,"消息队列"),
        JOB(4,"定时任务"),
        CONTAINER(5,"容器"),
        OTHER(6,"其他"),
        GATEWAY(7,"网关");

        String value;

        Integer code;

        private MiddleWareTypeEnum(Integer code,String value) {
            this.value = value;
            this.code = code;
        }

        public String value() {
            return this.value;
        }

    public static MiddleWareTypeEnum getEnumByValue(String value) {
        return Arrays.stream(values())
                .filter(middleWareTypeEnum -> middleWareTypeEnum.getValue().equals(value))
                .findFirst().orElse(null);
    }

    public static String getValueByCode(Integer code) {
        return Arrays.stream(values())
                .filter(middleWareTypeEnum -> middleWareTypeEnum.getCode().equals(code))
                .findFirst().orElse(null)
                .getValue();
    }
}
