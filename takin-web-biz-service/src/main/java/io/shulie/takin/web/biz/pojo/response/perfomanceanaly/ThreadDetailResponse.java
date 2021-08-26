package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 线程列表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:27
 */
@Data
public class ThreadDetailResponse implements Serializable {
    private static final long serialVersionUID = -4546154536371347630L;

    private String threadName;

    private BigDecimal threadCpuUseRate;

    private List<String> threadStack;

    private Long threadStackLink;

    private String threadStatus;
}
