package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 线程cpu图表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("线程分析图表返回值")
public class ThreadCpuChartResponse extends TimeChartResponse {
    private static final long serialVersionUID = 6592376201693158275L;

    @ApiModelProperty(value = "线程数量")
    private Integer threadCount;

    @ApiModelProperty(value = "cpu使用率")
    private BigDecimal cpuRate;

    @ApiModelProperty(value = "基础信息id")
    private String baseId;

    private Long timestamp;
}
