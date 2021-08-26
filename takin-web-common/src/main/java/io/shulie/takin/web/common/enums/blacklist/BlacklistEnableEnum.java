package io.shulie.takin.web.common.enums.blacklist;

/**
 * @author 无涯
 * @date 2021/4/6 4:46 下午
 */
public enum BlacklistEnableEnum {
    //是否可用(0表示1表示启动,2表示启用未校验)
    DISABLED(0, "未启动"),
    ENABLE(1, "启动"),
    ENABLE_NOT_VERIFIED(2, "启用未校验");


    private Integer status;
    private String desc;

    BlacklistEnableEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDescByType(Integer status) {
        for (BlacklistEnableEnum enableEnum : BlacklistEnableEnum.values()) {
            if (enableEnum.status.equals(status)) {
                return enableEnum.desc;
            }
        }
        return "";
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
