package com.pamirs.takin.common.enums.ds;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: 南风
 * @Date: 2021/8/31 3:49 下午
 * MQ support
 */
@AllArgsConstructor
@Getter
public enum MQTypeEnum {

    ROCKETMQ("1","ROCKETMQ"),

    RABBITMQ("2","RABBITMQ"),

    KAFKA("3","KAFKA");


    private String code;

    private String value;

    /**
     * 根据 value 获得枚举
     * @param value value
     * @return 枚举
     */
    public static MQTypeEnum getEnumByValue(String value) {
        return Arrays.stream(values())
                .filter(mqTypeEnum -> mqTypeEnum.getValue().equals(value))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code 获得枚举
     * @param code code
     * @return 枚举
     */
    public static MQTypeEnum getEnumByCode(String code) {
        return Arrays.stream(values())
                .filter(mqTypeEnum -> mqTypeEnum.getCode().equals(code))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code, 获得描述
     * @param code code
     * @return 描述
     */
    public static String getDescByCode(String code) {
        return Arrays.stream(values())
                .filter(mqTypeEnum -> mqTypeEnum.getCode().equals(code))
                .findFirst()
                .map(MQTypeEnum::getCode).orElse("");
    }

    /**
     * 根据 value, 获得code
     * @param value value
     * @return 描述
     */
    public static String getCodeByValue(String value) {
        return Arrays.stream(values())
                .filter(mqTypeEnum -> mqTypeEnum.getValue().equals(value))
                .findFirst()
                .map(MQTypeEnum::getValue).orElse("");
    }
}
