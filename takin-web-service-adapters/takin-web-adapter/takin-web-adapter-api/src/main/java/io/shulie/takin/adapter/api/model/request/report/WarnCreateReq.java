package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建告警
 *
 * @author qianshui
 * @date 2020/11/18 上午11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnCreateReq extends ContextExt {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "场景ID")
    private Long ptId;

    @ApiModelProperty(value = "SlaID")
    private Long slaId;

    @ApiModelProperty(value = "Sla名称")
    private String slaName;

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "业务活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "告警内容")
    private String warnContent;

    private Double realValue;

    private String bindRef;

    private String warnTime;
}
