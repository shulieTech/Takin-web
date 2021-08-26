package io.shulie.takin.web.config.enums;

/**
 * @author shiyajian
 * create: 2020-09-15
 */
public enum ShadowConsumerType {
    /**
     * Rocket消息
     */
    ROCKETMQ,
    /**
     * kafka消息
     */
    KAFKA,
    /**
     * rabbit消息
     */
    RABBITMQ;

    public static ShadowConsumerType of(String name) {
        for (ShadowConsumerType enumConstant : ShadowConsumerType.class.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(name)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("不正确的类型名称：" + name);
    }
}
