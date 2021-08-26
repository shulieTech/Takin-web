package io.shulie.takin.web.biz.pojo.openapi.response.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel
public class WarnDetailOpenApiResp implements Serializable {

    @ApiModelProperty("报告 ID")
    private Long reportId;
    @ApiModelProperty("SLA ID")
    private Long slaId;
    @ApiModelProperty("SLA名称")
    private String slaName;
    @ApiModelProperty("活动ID")
    private Long businessActivityId;
    @ApiModelProperty("活动名称")
    private String businessActivityName;
    @ApiModelProperty("规则明细")
    private String content;
    @ApiModelProperty("警告时间")
    private String warnTime;
}
