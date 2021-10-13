package io.shulie.takin.web.common.enums.shadow;

import java.util.Arrays;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
public enum ShadowMqConsumerType {
    ROCKETMQ,
    KAFKA,
    RABBITMQ;

    /**
     * 根据名字获得枚举
     *
     * @param name 枚举名字
     * @return 枚举
     */
    public static ShadowMqConsumerType getByName(String name) {
        return Arrays.stream(values()).filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static ShadowMqConsumerType of(String name) {
        for (ShadowMqConsumerType enumConstant : ShadowMqConsumerType.class.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(name)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("不正确的类型名称：" + name);
    }
}
