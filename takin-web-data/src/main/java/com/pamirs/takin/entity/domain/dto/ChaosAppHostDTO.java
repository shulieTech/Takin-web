package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 应用节点
 * @author 710524
 * @ClassName: ChaosAppHostVO
 * @Package: com.monitor.platform.bean.vo
 * @date 2019/5/5 0005 14:18
 */

public class ChaosAppHostDTO implements Serializable {

    private Long id;

    private String name;

    private String ip;

    private String appCode;

    private String appName;

    private String port;

    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    public ChaosAppHostDTO() {
    }

    public ChaosAppHostDTO(Long id, Long appCode, String appName, String name, String ip, String port, String status,
        Date createTime) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.appCode = String.valueOf(appCode);
        this.appName = appName;
        this.port = port;
        this.status = status;
        this.createTime = createTime;
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

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
