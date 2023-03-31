package io.shulie.takin.cloud.common.enums.pts;

/**
 * 断言类型枚举
 */
public enum PtsAssertTypeEnum {

    RESPONSE_BODY("响应Body","Assertion.response_data", "整个body"),
    RESPONSE_CODE("响应状态码","Assertion.response_code", "状态码"),
    PARAMS("出参","params", ""),
    UNKNOW("未知","unknow", "");


    private String name;

    private String value;

    private String text;

    PtsAssertTypeEnum(String name, String value, String text) {
        this.name = name;
        this.value = value;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static PtsAssertTypeEnum getByName(String name) {
        for(PtsAssertTypeEnum var : PtsAssertTypeEnum.values()) {
            if(var.getName().equals(name)) {
                return var;
            }
        }
        return UNKNOW;
    }

    public static PtsAssertTypeEnum getByValue(String value) {
        for(PtsAssertTypeEnum var : PtsAssertTypeEnum.values()) {
            if(var.getValue().equals(value)) {
                return var;
            }
        }
        return UNKNOW;
    }
}
