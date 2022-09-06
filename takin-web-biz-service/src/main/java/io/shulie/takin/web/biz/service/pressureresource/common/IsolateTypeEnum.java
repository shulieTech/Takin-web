package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum IsolateTypeEnum {
    SHADOW_DB(1, "影子库"),
    SHADOW_DB_TABLE(2, "影子库/影子表"),
    SHADOW_TABLE(3, "影子表");

    int code;
    String desc;

    IsolateTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
}
