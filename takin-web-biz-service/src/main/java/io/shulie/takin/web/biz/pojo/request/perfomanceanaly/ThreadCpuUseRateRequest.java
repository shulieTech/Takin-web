package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ThreadCpuUseRateRequest
 * @Description 线程cpu利用率
 * @author qianshui
 * @Date 2020/11/4 上午11:30
 */
@Data
@ApiModel("线程cpu占用率入参")
public class ThreadCpuUseRateRequest extends PerformanceCommonRequest implements Serializable {
    private static final long serialVersionUID = -7182637518514949578L;

    @ApiModelProperty(value = "报告id")
    private Long reportId;

    @ApiModelProperty(value = "线程名称")
    private String threadName;

}
