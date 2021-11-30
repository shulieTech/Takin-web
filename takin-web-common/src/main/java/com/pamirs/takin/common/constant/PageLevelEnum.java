package com.pamirs.takin.common.constant;

/**
 * //1普通页面加载 2简单查询页面/复杂界面 3复杂查询页面
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月16日
 */
public enum PageLevelEnum {
    //1普通页面加载 2简单查询页面/复杂界面 3复杂查询页面
    PAGE_LEVEL_SERIAL("1", "普通页面加载", 3000),
    PAGE_LEVEL_SIMPLE("2", "简单查询页面/复杂界面", 5000),
    PAGE_LEVEL_COMPLEX("3", "复杂查询页面", 8000);

    private String code;
    private String name;
    private int time;

    /**
     * 构造方法
     *
     * @param name
     */
    PageLevelEnum(String code, String name, int time) {
        this.code = code;
        this.name = name;
        this.time = time;
    }

    public static String getName(String code) {
        for (PageLevelEnum level : PageLevelEnum.values()) {
            if (level.code.equals(code)) {
                return level.name;
            }
        }
        return "未知";
    }

    public static PageLevelEnum getPageLevelEnum(String code) {
        for (PageLevelEnum level : PageLevelEnum.values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public int getTime() {
        return this.time;
    }

    public String getName() {
        return name;
    }

}
