package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum JoinFlagEnum {
    YES(0, "加入"),
    NO(1, "未加入");

    int code;
    String name;

    JoinFlagEnum(int code, String name) {
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
        JoinFlagEnum[] isolateTypeEnums = JoinFlagEnum.values();
        for (JoinFlagEnum isolateTypeEnum : isolateTypeEnums) {
            if (isolateTypeEnum.getCode() == (code)) {
                return isolateTypeEnum.getName();
            }
        }
        return JoinFlagEnum.YES.getName();
    }
}
