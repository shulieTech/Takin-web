package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * GC次数图表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GcCountChartResponse extends TimeChartResponse {
    private static final long serialVersionUID = -5933341439641660209L;

    private Integer youngGcCount;

    private Integer fullGcCount;
}
