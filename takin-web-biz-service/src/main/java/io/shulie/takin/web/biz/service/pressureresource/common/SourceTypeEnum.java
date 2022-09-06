package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 3:43 PM
 */
public enum SourceTypeEnum {
    MANUAL(0, "手工新增"),
    AUTO(1, "系统自动");

    private int code;
    private String desc;

    SourceTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
}
