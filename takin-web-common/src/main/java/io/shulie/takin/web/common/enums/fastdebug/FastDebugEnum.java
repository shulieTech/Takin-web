package io.shulie.takin.web.common.enums.fastdebug;

/**
 * @author 无涯
 * @date 2020/12/28 4:03 下午
 */
public enum FastDebugEnum {
    FREE(0, "未调试"),
    STARTING(1, "调试中");
    private Integer status;
    private String desc;

    FastDebugEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDescByStatus(Integer status) {
        for (FastDebugEnum debugEnum : FastDebugEnum.values()) {
            if (debugEnum.status.equals(status)) {
                return debugEnum.desc;
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
