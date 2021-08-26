package com.pamirs.takin.common.constant;

/**
 * @author fanxx
 * @date 2021/1/6 10:32 上午
 */
public enum VerifyTypeEnum {
    /**
     * 压测场景
     */
    SCENE(0),

    /**
     * 业务流程
     */
    FLOW(1),

    /**
     * 业务活动
     */
    ACTIVITY(2);

    private Integer code;

    VerifyTypeEnum(Integer code) {
        this.code = code;
    }

    public static VerifyTypeEnum getTypeByCode(Integer code) {
        for (VerifyTypeEnum verifyTypeEnum : VerifyTypeEnum.values()) {
            if (verifyTypeEnum.getCode().equals(code)) {
                return verifyTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
