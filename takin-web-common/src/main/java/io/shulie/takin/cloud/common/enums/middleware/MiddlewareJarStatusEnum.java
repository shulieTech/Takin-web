package io.shulie.takin.cloud.common.enums.middleware;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 中间件支持状态枚举
 *
 * @author liuchuan
 * @date 2021/6/1 2:26 下午
 */
@AllArgsConstructor
@Getter
public enum MiddlewareJarStatusEnum {

    /**
     * 无需支持
     */
    NO_REQUIRED(3, "无需支持"),

    TO_BE_VERIFIED(4, "待验证"),

    TO_BE_SUPPORTED(2, "待支持"),

    SUPPORTED(1, "已支持");

    private final Integer code;

    private final String desc;

    /**
     * 通过 desc 获得枚举
     *
     * @param desc 描述
     * @return 枚举
     */
    public static MiddlewareJarStatusEnum getByDesc(String desc) {
        return Arrays.stream(values())
            .filter(middlewareJarStatusEnum -> middlewareJarStatusEnum.desc.equals(desc))
            .findFirst().orElse(null);
    }

    /**
     * 通过 code 获得枚举
     *
     * @param code 状态
     * @return 枚举
     */
    public static MiddlewareJarStatusEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(middlewareJarStatusEnum -> middlewareJarStatusEnum.code.equals(code))
            .findFirst().orElse(null);
    }

}
