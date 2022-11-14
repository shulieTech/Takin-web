package io.shulie.takin.web.biz.service.pressureresource;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum FinishStatusEnum {
    NO(0, "未开始"),
    START_ING(1, "进行中"),
    FINSH(2, "完成");

    int code;
    String name;

    FinishStatusEnum(int code, String name) {
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
        FinishStatusEnum[] isolateTypeEnums = FinishStatusEnum.values();
        for (FinishStatusEnum isolateTypeEnum : isolateTypeEnums) {
            if (isolateTypeEnum.getCode() == (code)) {
                return isolateTypeEnum.getName();
            }
        }
        return FinishStatusEnum.NO.getName();
    }
}
