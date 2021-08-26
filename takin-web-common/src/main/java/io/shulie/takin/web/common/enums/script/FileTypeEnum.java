package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxx
 * @date 2021/3/11 4:08 下午
 */
@AllArgsConstructor
@Getter
public enum FileTypeEnum {

    /**
     * 脚本文件
     */
    SCRIPT(0),

    /**
     * 数据文件
     */
    DATA(1),

    /**
     * 附件
     */
    ATTACHMENT(2);

    private final Integer code;

}
