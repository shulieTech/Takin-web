package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脚本调试记录失败类型枚举
 *
 * @author liuchuan
 * @date 2021/5/11 3:24 下午
 */
@AllArgsConstructor
@Getter
public enum ScriptDebugFailedTypeEnum {

    /**
     * 启动通知超时失败
     */
    FAILED_TIMEOUT(10),

    /**
     * 漏数失败
     */
    FAILED_LEAK(20),

    /**
     * 响应失败, 非 200
     */
    FAILED_RESPONSE(30),

    /**
     * 其他错误
     */
    FAILED_OTHER(100);

    private final Integer code;

}
