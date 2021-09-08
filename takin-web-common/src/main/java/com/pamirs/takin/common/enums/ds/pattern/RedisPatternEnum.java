package com.pamirs.takin.common.enums.ds.pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: 南风
 * @Date: 2021/8/30 8:26 下午
 * redis 部署模式
 */
@AllArgsConstructor
@Getter
public enum RedisPatternEnum {

    /**
     * 主从
     */
    MASTER_SLAVE(0, "主从"),

    /**
     * 哨兵
     */
    SENTRY(1, "哨兵"),

    /**
     * 影子Server
     */
    CLUSTER(2, "集群");



    private final Integer code;

    private final String desc;


    /**
     * 根据 desc 获得枚举
     * @param desc desc
     * @return 枚举
     */
    public static RedisPatternEnum getEnumByDesc(String desc) {
        return Arrays.stream(values())
                .filter(redisPatternEnum -> redisPatternEnum.getDesc().equals(desc))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code 获得枚举
     * @param code code
     * @return 枚举
     */
    public static RedisPatternEnum getEnumByCode(Integer code) {
        return Arrays.stream(values())
                .filter(redisPatternEnum -> redisPatternEnum.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
