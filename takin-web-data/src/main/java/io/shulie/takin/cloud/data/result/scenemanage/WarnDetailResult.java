package io.shulie.takin.cloud.data.result.scenemanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
public class WarnDetailResult {

    @ApiModelProperty(value = "报告 ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "SLA名称")
    private String slaName;

    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "规则明细")
    private String content;

    @ApiModelProperty(value = "警告时间")
    private String warnTime;
}
