package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;

/**
 * 应用节点
 *
 * @author 710524
 * @date 2019/5/5 0005 14:18
 */

public class ChaosAppHostVO implements Serializable {

    private Long id;

    private String name;

    private String ip;

    private Long appCode;

    private String appName;

    private String port;

    public ChaosAppHostVO() {
    }

    public ChaosAppHostVO(Long appCode, String name, String ip, String port) {
        this.name = name;
        this.ip = ip;
        this.appCode = appCode;
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getAppCode() {
        return appCode;
    }

    public void setAppCode(Long appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
