package io.shulie.takin.cloud.common.enums;

/**
 * @author fanxx
 * @date 2020/5/13 下午3:03
 */
public enum OpenTypeEnum {
    /**
     * 开通模式：1、长期开通 2、短期抢占
     */
    fix(1, "长期开通"),
    temporary(2, "短期抢占");
    private Integer code;
    private String name;

    OpenTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getStatus() {
        return name;
    }

    public void setStatus(String status) {
        this.name = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
