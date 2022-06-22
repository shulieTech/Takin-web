package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnQueryReq extends ContextExt {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "节点xpathMd5值")
    private String bindRef;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数")
    private int pageSize = 20;

    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码")
    private int current = 0;
}
