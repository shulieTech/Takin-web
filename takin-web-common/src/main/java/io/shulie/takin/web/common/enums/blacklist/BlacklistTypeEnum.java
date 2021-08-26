package io.shulie.takin.web.common.enums.blacklist;

/**
 * @author 无涯
 * @date 2021/4/6 2:11 下午
 */
public enum BlacklistTypeEnum {
    REDIS(0, "Redis");



    private Integer type;
    private String desc;

    BlacklistTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String getDescByType(Integer type) {
        for (BlacklistTypeEnum typeEnum : BlacklistTypeEnum.values()) {
            if (typeEnum.type.equals(type)) {
                return typeEnum.desc;
            }
        }
        return "";
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
