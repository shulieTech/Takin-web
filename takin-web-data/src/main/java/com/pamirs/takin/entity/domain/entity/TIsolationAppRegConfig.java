package com.pamirs.takin.entity.domain.entity;

public class TIsolationAppRegConfig extends BaseEntity {

    /**
     * 注册中心类型
     */
    private String regType;

    /**
     * 注册中心地址
     */
    private String regAddr;

    /**
     * 注册中心ID
     */
    private Long regId;

    /**
     * 是否启用
     */
    private Boolean enable;

    public String getRegType() {
        return regType;
    }

    public void setRegType(String regType) {
        this.regType = regType;
    }

    public String getRegAddr() {
        return regAddr;
    }

    public void setRegAddr(String regAddr) {
        this.regAddr = regAddr;
    }

    public Long getRegId() {
        return regId;
    }

    public void setRegId(Long regId) {
        this.regId = regId;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
