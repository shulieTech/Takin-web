package io.shulie.takin.web.biz.service.pressureresource.common;

import com.pamirs.takin.common.enums.ds.DsTypeEnum;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum IsolateTypeEnum {
    DEFAULT(0, "无"),
    SHADOW_DB(1, "影子库"),
    SHADOW_DB_TABLE(2, "影子库-影子表"),
    SHADOW_TABLE(3, "影子表");

    int code;
    String name;

    IsolateTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getName(int code) {
        IsolateTypeEnum[] isolateTypeEnums = IsolateTypeEnum.values();
        for (IsolateTypeEnum isolateTypeEnum : isolateTypeEnums) {
            if (isolateTypeEnum.getCode() == (code)) {
                return isolateTypeEnum.getName();
            }
        }
        return IsolateTypeEnum.DEFAULT.getName();
    }

    private static String keyPre = "隔离方案-";

    public static String getKeyName(int code) {
        return keyPre + getName(code);
    }

    public static int convertDsType(Integer isolateType) {
        if (isolateType == SHADOW_DB.getCode()) {
            return DsTypeEnum.SHADOW_DB.getCode();
        }
        if (isolateType == SHADOW_DB_TABLE.getCode()) {
            return DsTypeEnum.SHADOW_REDIS_SERVER.getCode();
        }
        if (isolateType == SHADOW_TABLE.getCode()) {
            return DsTypeEnum.SHADOW_TABLE.getCode();
        }
        throw new RuntimeException("未有配置值,请检查隔离类型{}");
    }
}