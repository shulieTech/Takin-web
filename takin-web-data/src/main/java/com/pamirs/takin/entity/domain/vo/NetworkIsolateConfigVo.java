package com.pamirs.takin.entity.domain.vo;

public class NetworkIsolateConfigVo {

    private int pageSize;

    private int pageNum;

    private String appName;

    private String vipAddr;

    private String balanceType;

    private String poolName;

    private long id;

    private String active;

    private String isOnline;

    private String disable;

    public String getDisable() {
        return disable;
    }

    public void setDisable(String disable) {
        this.disable = disable;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVipAddr() {
        return vipAddr;
    }

    public void setVipAddr(String vipAddr) {
        this.vipAddr = vipAddr;
    }

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

}
