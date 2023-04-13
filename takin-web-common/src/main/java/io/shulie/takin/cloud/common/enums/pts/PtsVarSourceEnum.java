package io.shulie.takin.cloud.common.enums.pts;

/**
 * 参数来源枚举
 */
public enum PtsVarSourceEnum {

    HEADERKV("Header:K/V","Header:K/V"),
    BODY_JSON("Body:JSON|TEXT","Body:JSON|TEXT"),
    UNKNOW("未知","unknow");


    private String name;

    private String value;

    PtsVarSourceEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static PtsVarSourceEnum getByName(String name) {
        for(PtsVarSourceEnum var : PtsVarSourceEnum.values()) {
            if(var.getName().equals(name)) {
                return var;
            }
        }
        return UNKNOW;
    }

    public static PtsVarSourceEnum getByValue(String value) {
        for(PtsVarSourceEnum var : PtsVarSourceEnum.values()) {
            if(var.getValue().equals(value)) {
                return var;
            }
        }
        return UNKNOW;
    }
}

