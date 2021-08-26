package io.shulie.takin.web.amdb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 调试流量明细记录请求入参类型枚举
 *
 * @author liuchuan
 * @date 2021/5/11 3:24 下午
 */
@AllArgsConstructor
@Getter
public enum LinkRequestResultTypeEnum {

    /**
     * 非200 失败
     */
    FAILED(0),

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 断言失败
     */
    FAILED_ASSERT(2);

    private final Integer code;

}
