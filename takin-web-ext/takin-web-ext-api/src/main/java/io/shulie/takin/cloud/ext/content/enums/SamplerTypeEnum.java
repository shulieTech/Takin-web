package io.shulie.takin.cloud.ext.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 数列科技
 */
@Getter
@AllArgsConstructor
public enum SamplerTypeEnum {

    /**
     * http请求
     */
    HTTP("HTTP", RpcTypeEnum.HTTP),
    /**
     * dubbo请求
     */
    DUBBO("DUBBO", RpcTypeEnum.DUBBO),
    //    /**
    //     * ROCKETMQ请求
    //     */
    //    ROCKETMQ("ROCKETMQ"),
    /**
     * RABBIT 请求
     */
    RABBITMQ("RABBITMQ", RpcTypeEnum.MQ),
    /**
     * KAFKA 请求
     */
    KAFKA("KAFKA", RpcTypeEnum.MQ),

    /**
     * JDBC 请求
     */
    JDBC("JDBC", RpcTypeEnum.DB),
    /**
     * 未知请求类型
     */
    UNKNOWN("UNKNOWN", RpcTypeEnum.UNKNOWN);

    private final String type;
    private final RpcTypeEnum rpcTypeEnum;
}
