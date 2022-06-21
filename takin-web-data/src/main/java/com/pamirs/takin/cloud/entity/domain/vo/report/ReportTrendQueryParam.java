package com.pamirs.takin.cloud.entity.domain.vo.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@ApiModel
public class ReportTrendQueryParam {

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
     * xpathMD5
     */
    @ApiModelProperty(value = "节点的xpathMD5值，即bindRef,老版本的压测场景是业务活动名称，新版本是md5值")
    private String xpathMd5;

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
