package io.shulie.takin.web.biz.pojo.openapi.request.report;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel
public class WarnQueryOpenApiReq extends PagingDevice {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;
}
