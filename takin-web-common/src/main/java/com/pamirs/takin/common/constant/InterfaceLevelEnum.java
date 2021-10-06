package com.pamirs.takin.common.constant;

/**
 * @author shulie
 * @version v1.0
 * @date 2018年5月16日
 */
public enum InterfaceLevelEnum {
    //接口类型：1简单操作/查询 2一般操作/查询 3复杂操作 4涉及级联嵌套调用多服务操作 5调用外网操作
    PAGE_LEVEL_SIMPLE("1", "简单操作/查询", 50),
    PAGE_LEVEL_GENERAL("2", "一般操作/查询", 100),
    PAGE_LEVEL_COMPLEX("3", "复杂操作", 300),
    PAGE_LEVEL_COMPLEX_MANY("4", "涉及级联嵌套调用多服务操作", 500),
    PAGE_LEVEL_COMPLEX_TRANS("5", "调用外网操作", 500);

    private String code;
    private String name;
    private int time;

    /**
     * 构造方法
     *
     * @param name
     */
    InterfaceLevelEnum(String code, String name, int time) {
        this.code = code;
        this.name = name;
        this.time = time;
    }

    public static String getName(String code) {
        for (InterfaceLevelEnum level : InterfaceLevelEnum.values()) {
            if (level.code.equals(code)) {
                return level.name;
            }
        }
        return "未知";
    }

    public static InterfaceLevelEnum getInterfaceLevelEnum(String code) {
        for (InterfaceLevelEnum level : InterfaceLevelEnum.values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getName("1"));
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return this.code;
    }

    public int getTime() {
        return this.time;
    }
}
