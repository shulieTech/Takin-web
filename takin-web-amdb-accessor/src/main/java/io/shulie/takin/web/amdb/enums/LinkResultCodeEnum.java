package io.shulie.takin.web.amdb.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 调试流量明细记录状态枚举
 *
 * @author liuchuan
 * @date 2021/5/11 3:24 下午
 */
@AllArgsConstructor
@Getter
public enum LinkResultCodeEnum {

    /**
     * 响应成功
     */
    SUCCESS("00", "成功"),
    SUCCESS_TWO_HUNDRED("200", "成功"),

    FAILED("01", "响应失败"),

    FAILED_DUBBO("02", "dubbo 错误"),

    TIME_OUT("03", "请求超时"),

    FAILED_UNKNOWN("04", "未知错误"),

    FAILED_ASSERT("05", "断言失败");

    /**
     * code
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 通过code获得枚举
     *
     * @param code code
     * @return 枚举
     */
    public static LinkResultCodeEnum getByCode(String code) {
        return Arrays.stream(values())
            .filter(linkResultCodeEnum -> linkResultCodeEnum.getCode().equals(code))
            .findFirst().orElse(null);
    }

}
