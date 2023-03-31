package io.shulie.takin.cloud.common.enums.pts;

public enum PtsAssertConditionEnum {

    CONTAIN("包含","16", "字符串"),
    EQUAL("等于","8", "相等"),
    REGEX("正则匹配","1", "匹配"),
    UNKNOW("未知","unknow","unknow");


    private String name;

    private String value;

    private String text;

    PtsAssertConditionEnum(String name, String value, String text) {
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

    public static PtsAssertConditionEnum getByName(String name) {
        for(PtsAssertConditionEnum var : PtsAssertConditionEnum.values()) {
            if(var.getName().equals(name)) {
                return var;
            }
        }
        return UNKNOW;
    }

    public static PtsAssertConditionEnum getByValue(String value) {
        for(PtsAssertConditionEnum var : PtsAssertConditionEnum.values()) {
            if(var.getValue().equals(value)) {
                return var;
            }
        }
        return UNKNOW;
    }
}
