package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脚本
 *
 * @author liuchuan
 * @date 2021/4/20 3:18 下午
 */
@AllArgsConstructor
@Getter
public enum ScriptManageDeployStatusEnum {

    /**
     * 新建
     */
    NEW(0, "新建"),
    PASS(1, "调试通过"),
    HISTORY(2, "历史版本");

    /**
     * 状态
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

}
