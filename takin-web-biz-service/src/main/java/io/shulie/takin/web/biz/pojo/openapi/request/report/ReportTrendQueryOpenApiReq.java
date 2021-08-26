package io.shulie.takin.web.biz.pojo.openapi.request.report;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel
public class ReportTrendQueryOpenApiReq extends WebRequest implements Serializable {

    /**
     * 报表ID
     */
    @ApiModelProperty(value = "报表ID")
    private Long reportId;

    /**
     * 场景ID
     */
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID，如果不指定活动ID则表示全局趋势")
    private Long businessActivityId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
