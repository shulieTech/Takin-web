package io.shulie.takin.web.api.request;

/**
 * 基础类
 * @author caijianying
 */
public class TakinWebBaseRequest {
    /**
     * 页码 从0开始
     */
    public int pageNo;

    public int pageSize;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
