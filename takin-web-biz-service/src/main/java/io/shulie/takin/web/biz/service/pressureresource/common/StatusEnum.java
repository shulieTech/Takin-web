package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum StatusEnum {
    NO(0, "未检测"),
    FAIL(1, "检测失败"),
    SUCCESS(2, "检测成功");

    int code;
    String name;

    StatusEnum(int code, String name) {
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
        StatusEnum[] isolateTypeEnums = StatusEnum.values();
        for (StatusEnum isolateTypeEnum : isolateTypeEnums) {
            if (isolateTypeEnum.getCode() == (code)) {
                return isolateTypeEnum.getName();
            }
        }
        return StatusEnum.NO.getName();
    }
}
