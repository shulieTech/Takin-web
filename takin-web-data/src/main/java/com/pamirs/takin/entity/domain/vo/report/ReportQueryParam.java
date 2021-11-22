package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class ReportQueryParam extends WebRequest implements Serializable {

    @ApiModelProperty(name = "sceneId", value = "场景ID")
    private Long sceneId;

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
    private String userName;

}
