package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * GC耗时图表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GcCostChartResponse extends TimeChartResponse {
    private static final long serialVersionUID = 5088342496850440875L;

    private Long youngGcCost;

    private Long fullGcCost;
}
