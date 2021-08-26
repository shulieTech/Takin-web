package io.shulie.takin.web.common.enums.application;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 应用中间件状态枚举
 *
 * @author liuchuan
 * @date 2021/5/11 3:24 下午
 */
@AllArgsConstructor
@Getter
public enum ApplicationMiddlewareStatusEnum {

    /**
     * 已支持
     */
    SUPPORTED(1, "已支持"),

    /**
     * 未支持
     */
    NOT_SUPPORTED(2, "未支持"),

    /**
     * 无需支持
     */
    NO_SUPPORT_REQUIRED(3, "无需支持"),

    /**
     * 未知
     */
    UNKNOWN(4, "未知"),

    /**
     * 无
     */
    NONE(0, "");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据 code 获取 枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static ApplicationMiddlewareStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }

        return Arrays.stream(values()).filter(applicationMiddlewareStatusEnum ->
            applicationMiddlewareStatusEnum.getCode().equals(code))
            .findFirst().orElse(null);
    }
    /**
     * 根据 code 获取 枚举
     *
     * @param desc 描述
     * @return 枚举
     */
    public static ApplicationMiddlewareStatusEnum getByDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            return null;
        }

        return Arrays.stream(values()).filter(applicationMiddlewareStatusEnum ->
            applicationMiddlewareStatusEnum.getDesc().equals(desc))
            .findFirst().orElse(null);
    }

}
