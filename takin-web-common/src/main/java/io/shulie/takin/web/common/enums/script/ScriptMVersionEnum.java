package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ScriptMVersionEnum {

    /**
     * 脚本m版本
     */
    SCRIPT_M_1(1),
    ;

    private final Integer code;

    public static boolean isM_1(Integer mVersion) {
        return SCRIPT_M_1.getCode().equals(mVersion);
    }
}
