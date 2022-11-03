package com.pamirs.takin.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 链路信息导出sheet所用的枚举
 */
@Getter
@AllArgsConstructor
public enum LinkSheetEnum {

    LINK_INFO("链路概览", 4, 0),

    LINK_APPLICATION_INFO("应用信息", 5, 1),

    LINK_REMOTE_CALL("远程调用", 7, 2),

    LINK_DB("数据库表", 9, 3),

    LINK_MQ("影子topic", 11, 4),
    ;

    private final String desc;

    private final Integer columnNum;

    /**
     * sheet 下标
     */
    private final Integer sheetNumber;
}
