package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;

import lombok.Data;

/**
 * 线程状态
 *
 * @author qianshui
 * @date 2020/11/4 上午11:26
 */
@Data
public class ThreadStatusResponse implements Serializable, Comparable<ThreadStatusResponse> {

    private static final long serialVersionUID = -5845702420534131372L;

    private String status;

    private Integer count;

    @Override
    public int compareTo(ThreadStatusResponse o) {
        int diff = o.getCount() - this.getCount();
        if (diff == 0) {
            return this.getStatus().compareTo(o.getStatus());
        }
        return diff;
    }
}
