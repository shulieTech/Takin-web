package io.shulie.takin.adapter.api.model.response.report;

import java.math.BigDecimal;
import java.util.Date;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai
 * @date 2020-11-02 17:05
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportResp extends ContextExt {

    @ApiModelProperty(value = "报告ID")
    private Long id;

    @ApiModelProperty(value = "消耗流量")
    private BigDecimal amount;

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "并发数")
    private Integer concurrent;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "压测结果")
    private int conclusion;

    @ApiModelProperty(value = "压测总计时")
    private String totalTime;

    // private String features

    @ApiModelProperty(value = "压测不通过的原因")
    private String errorMsg;

    private Long scriptId;

}
