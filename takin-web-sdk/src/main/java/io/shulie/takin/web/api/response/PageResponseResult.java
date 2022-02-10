package io.shulie.takin.web.api.response;

/**
 * @author caijianying
 */
public class PageResponseResult<T> extends ResponseResult<T>{
    /**
     * 记录总数
     */
    private long total;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
