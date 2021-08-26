package com.pamirs.takin.entity.domain.vo;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TIsolationAppRegConfig;

public class TIsolationAppRegConfigVO extends TIsolationAppRegConfig {

    private List<Long> regIds;

    private Integer pageNum;

    private Integer pageSize;

    public List<Long> getRegIds() {
        return regIds;
    }

    public void setRegIds(List<Long> regIds) {
        this.regIds = regIds;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
