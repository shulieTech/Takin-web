package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.web.ext.entity.UserCommonExt;

/**
 * 查询分页信息
 *
 * @author 710524
 * @date 2019/5/9 0009 17:48
 */
public abstract class AbstractQueryPage extends UserCommonExt implements Serializable {

    private int pageNum;
    private int pageSize;
    private String orderBy = "id desc";

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = Math.max(1, pageNum);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
