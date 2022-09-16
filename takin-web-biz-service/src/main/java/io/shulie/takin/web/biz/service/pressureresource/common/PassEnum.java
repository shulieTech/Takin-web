package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum PassEnum {
    PASS_YES(0, "是"),
    PASS_NO(1, "否");

    int code;
    String name;

    PassEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
