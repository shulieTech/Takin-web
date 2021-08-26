package com.pamirs.takin.common.constant;

/**
 * @author fanxx
 * @date 2021/4/14 8:08 下午
 */
public enum ScriptConfigTypeEnum {

    /**
     * 业务活动
     */
    ACTIVITY(1),

    /**
     * 业务流程
     */
    BUSINESS_FLOW(2);

    private Integer code;

    ScriptConfigTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
