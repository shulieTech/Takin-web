package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum CheckStatusEnum {
    CHECK_NO(0, "未检测"),
    CHECK_ING(1, "检测中"),
    CHECK_FIN(2, "检测完成");

    int code;
    String name;

    CheckStatusEnum(int code, String name) {
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
        CheckStatusEnum[] isolateTypeEnums = CheckStatusEnum.values();
        for (CheckStatusEnum isolateTypeEnum : isolateTypeEnums) {
            if (isolateTypeEnum.getCode() == (code)) {
                return isolateTypeEnum.getName();
            }
        }
        return CheckStatusEnum.CHECK_NO.getName();
    }
}