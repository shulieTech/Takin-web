package io.shulie.takin.web.biz.service.pressureresource.common;

import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 3:43 PM
 */
public enum ResourceTypeEnum {
    MANUAL(1, "手工新增"),
    AUTO(2, "业务流程自动新增");

    private int code;
    private String desc;

    ResourceTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
}
