package io.shulie.takin.cloud.common.enums.scenemanage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liuchuan
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
