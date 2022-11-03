package com.pamirs.takin.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 链路信息导出sheet所用的枚举
 */
@Getter
@AllArgsConstructor
public enum LinkSheetEnum {

    LINK_INFO("链路概览"),

    LINK_APPLICATION_INFO("应用信息"),

    LINK_REMOTE_CALL("远程调用"),

    LINK_DB("数据库表"),

    LINK_MQ("影子topic"),
    ;

    private final String desc;

}
