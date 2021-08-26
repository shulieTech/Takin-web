package io.shulie.takin.web.amdb.bean.common;

import java.io.Serializable;

/**
 * @author fanxx
 * @date 2020/10/20 2:20 下午
 */
public class PagingDevice implements Serializable {
    private static final long serialVersionUID = 1L;
    private int pageSize = 20;
    private int currentPage = 0;

    public PagingDevice() {
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        currentPage = currentPage == null ? 0 : currentPage;
        this.currentPage = currentPage < 0 ? 0 : currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize == null ? this.getPageSize() : pageSize;
    }
}
