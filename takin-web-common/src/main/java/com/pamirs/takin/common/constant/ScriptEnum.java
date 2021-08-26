package com.pamirs.takin.common.constant;

/**
 * @author 无涯
 * @date 2020/12/3 10:32 上午
 */
public enum  ScriptEnum {

    JMETER("Jmeter", 0),
    SHELL("Shell", 1);

    private String name;
    private Integer value;

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    ScriptEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
    public static String getName(Integer value) {
        for(ScriptEnum scriptEnum:values()) {
            if(value.equals(scriptEnum.getValue())) {
                return scriptEnum.getName();
            }
        }
        return String.valueOf(value);
    }

}
