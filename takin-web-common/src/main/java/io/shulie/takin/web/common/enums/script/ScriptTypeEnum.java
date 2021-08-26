package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脚本类型枚举
 *
 * @author liuchuan
 * @date 2021/4/20 3:18 下午
 */
@AllArgsConstructor
@Getter
public enum ScriptTypeEnum {

    /**
     * jmeter 脚本
     */
    JMETER(0);

    /**
     * 状态
     */
    private final Integer code;

}
