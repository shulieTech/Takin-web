package io.shulie.takin.web.common.enums.shadow;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
public enum ShadowMqConsumerType {
    ROCKETMQ,
    KAFKA,
    RABBITMQ;

    public static ShadowMqConsumerType of(String name) {
        for (ShadowMqConsumerType enumConstant : ShadowMqConsumerType.class.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(name)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("不正确的类型名称：" + name);
    }
}
