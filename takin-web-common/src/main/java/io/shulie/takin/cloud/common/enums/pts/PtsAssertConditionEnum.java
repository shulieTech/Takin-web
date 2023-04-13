package io.shulie.takin.cloud.common.enums.pts;

/**
 * Pattern Matching Rules：
 *
 * 1.Contains
 *
 * 如果文本包含正则表达式模式，则为true
 *
 * 2.Matches
 *
 * 如果整个文本与正则表达式模式匹配，则为true
 *
 * 3.Equals
 *
 * 如果整个文本等于模式字符串(区分大小写)，则为true
 *
 * 4.Substring
 *
 * 如果文本包含模式字符串(区分大小写)，则为true
 */
public enum PtsAssertConditionEnum {

    CONTAIN("包含","16", "字符串"),
    EQUAL("等于","8", "相等"),
    REGEX("正则匹配","2", "包括"),
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
