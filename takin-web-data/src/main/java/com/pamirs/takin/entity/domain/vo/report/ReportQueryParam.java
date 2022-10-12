package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.common.beans.page.PagingDevice;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class ReportQueryParam extends PagingDevice implements Serializable {

    @ApiModelProperty(name = "sceneId", value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(name = "reportId", value = "报告ID")
    private Long reportId;

    /**
     * 场景名称
     */
    @ApiModelProperty(name = "sceneName", value = "场景名称")
    private String sceneName;

    /**
     * 压测开始时间
     */
    @ApiModelProperty(value = "压测开始时间")
    private String startTime;

    /**
     * 压测结束时间
     */
    @ApiModelProperty(value = "压测结束时间")
    private String endTime;

    /**
     * 前端传
     */
    @ApiModelProperty(value = "负责人姓名")
    private String managerName;
    /**
     * 报告开始时间
     */
    @ApiModelProperty(value = "报告开始时间")
    private String reportStartTime;

    /**
     * 报告结束时间
     */
    @ApiModelProperty(value = "报告结束时间")
    private String reportEndTime;

    @ApiModelProperty(value = "部门id")
    private Long deptId;
}
