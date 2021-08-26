package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脚本调试记录状态枚举
 *
 * @author liuchuan
 * @date 2021/5/11 3:24 下午
 */
@AllArgsConstructor
@Getter
public enum ScriptDebugStatusEnum {

    /**
     * 未启动
     */
    NOT_START(0),

    /**
     * 启动中
     */
    STARTING(1),

    /**
     * 请求中
     */
    REQUESTING(2),

    /**
     * 请求结束
     */
    REQUEST_END(3),

    /**
     * 调试成功
     */
    SUCCESS(4),

    /**
     * 调试失败
     */
    FAILED(5);

    private final Integer code;

}
