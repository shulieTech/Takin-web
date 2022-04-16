package com.pamirs.takin.cloud.entity.domain.dto.report;

import java.math.BigDecimal;
import java.util.Date;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CloudReportDTO extends ContextExt {

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
    private Integer conclusion;

    @ApiModelProperty(value = "压测总计时")
    private String totalTime;

    @ApiModelProperty(value = "压测不通过的原因")
    private String errorMsg;

}
