package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;

import com.pamirs.takin.entity.domain.entity.MqIsolateConfig;

public class MqIsolateConfigVo extends MqIsolateConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int pageSize;

    private int pageNum;

    /**
     * 0:隔离，1:隔离退出
     */
    private String isolationType;
    /**
     * 0:隔离前停写，1:隔离退出前停写
     */
    private String stopWriteStep;

    public String getStopWriteStep() {
        return stopWriteStep;
    }

    public void setStopWriteStep(String stopWriteStep) {
        this.stopWriteStep = stopWriteStep;
    }

    public String getIsolationType() {
        return isolationType;
    }

    public void setIsolationType(String isolationType) {
        this.isolationType = isolationType;
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

}
