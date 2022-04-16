package io.shulie.takin.adapter.api.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/3 11:46 上午
 */
@Data
public class WarnBean {

    @ApiModelProperty(value = "报告 ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "SLA名称")
    private String slaName;

    @ApiModelProperty(value = "活动ID")
    private String businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "警告次数")
    private Long total;

    @ApiModelProperty(value = "规则明细")
    private String content;

    @ApiModelProperty(value = "节点xpathMD5值")
    private String bindRef;

    @ApiModelProperty(value = "最新警告时间")
    private String lastWarnTime;

}
