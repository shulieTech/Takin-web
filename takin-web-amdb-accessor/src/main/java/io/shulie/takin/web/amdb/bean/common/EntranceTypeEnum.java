package io.shulie.takin.web.amdb.bean.common;

import lombok.Getter;

@Getter
public enum EntranceTypeEnum {
    // TYPE 名称和 AMDB 的 middleWareName 保持一致
    HTTP("HTTP"),
    DUBBO("DUBBO"),
    ROCKETMQ("ROCKETMQ"),
    RABBITMQ("RABBITMQ"),
    KAFKA("KAFKA"),
    ELASTICJOB("ELASTICJOB"),
    GRPC("GRPC");

    private String type;

    EntranceTypeEnum(String type) {
        this.type = type;
    }

    public static EntranceTypeEnum getEnumByType(String value) {
        EntranceTypeEnum[] enumConstants = EntranceTypeEnum.class.getEnumConstants();
        for (EntranceTypeEnum enumConstant : enumConstants) {
            if (enumConstant.getType().equalsIgnoreCase(value)) {
                return enumConstant;
            }
        }
        return HTTP;
    }

    public static EntranceTypeEnum getEnumByName(String name) {
        EntranceTypeEnum[] enumConstants = EntranceTypeEnum.class.getEnumConstants();
        for (EntranceTypeEnum enumConstant : enumConstants) {
            if (enumConstant.name().equalsIgnoreCase(name)) {
                return enumConstant;
            }
        }
        return HTTP;
    }
}
