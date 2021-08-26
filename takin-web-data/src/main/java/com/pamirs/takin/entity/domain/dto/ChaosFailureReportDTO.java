package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;

/**
 * 检测报告
 *
 * @author 710524
 * @date 2019年 05月16日 09:50
 */
public class ChaosFailureReportDTO implements Serializable {

    private Long id;

    private String commandName;

    private String aswNo;

    private Integer pressureCount;

    private Integer continuousTime;

    private Integer actualErrorCount;

    private Integer allowErrorCount;

    /**
     * 检测结果 0-未开始 1-检测中 2-通过 3-未通过
     */
    private Integer status;
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getAswNo() {
        return aswNo;
    }

    public void setAswNo(String aswNo) {
        this.aswNo = aswNo;
    }

    public Integer getPressureCount() {
        return pressureCount;
    }

    public void setPressureCount(Integer pressureCount) {
        this.pressureCount = pressureCount;
    }

    public Integer getContinuousTime() {
        return continuousTime;
    }

    public void setContinuousTime(Integer continuousTime) {
        this.continuousTime = continuousTime;
    }

    public Integer getActualErrorCount() {
        return actualErrorCount;
    }

    public void setActualErrorCount(Integer actualErrorCount) {
        this.actualErrorCount = actualErrorCount;
    }

    public Integer getAllowErrorCount() {
        return allowErrorCount;
    }

    public void setAllowErrorCount(Integer allowErrorCount) {
        this.allowErrorCount = allowErrorCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public ChaosFailureReportDTO setMessage(String message) {
        this.message = message;
        return this;
    }
}
