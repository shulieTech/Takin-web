package com.pamirs.takin.common.enums.ds;

import lombok.AllArgsConstructor;

/**
 * 配置模板枚举,当前为应用中的模板类型
 * @author zhaoyong
 */
@AllArgsConstructor
public enum ConfigTemplateEnum {

    DB_HBASE_CLIENT("DB_HBASE_CLIENT", "HBASE集群模式"),
    DB_ES_CLIENT("DB_ES_CLIENT", "ES集群模式"),

    ;

    private final String code;

    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
