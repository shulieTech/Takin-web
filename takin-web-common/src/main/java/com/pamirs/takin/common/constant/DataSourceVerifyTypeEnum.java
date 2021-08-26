package com.pamirs.takin.common.constant;

/**
 * @author fanxx
 * @date 2020/12/29 2:53 下午
 */
public enum DataSourceVerifyTypeEnum {
    MYSQL(0);

    private Integer code;

    DataSourceVerifyTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static DataSourceVerifyTypeEnum getTypeByCode(Integer code) {
        for (DataSourceVerifyTypeEnum typeEnum : DataSourceVerifyTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
