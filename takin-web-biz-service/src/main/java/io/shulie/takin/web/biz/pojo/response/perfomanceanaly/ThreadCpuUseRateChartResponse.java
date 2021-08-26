package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 线程cpu利用率图表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThreadCpuUseRateChartResponse extends TimeChartResponse {
    private static final long serialVersionUID = -6280051083663598217L;

    private Double threadCpuUseRate;
}
