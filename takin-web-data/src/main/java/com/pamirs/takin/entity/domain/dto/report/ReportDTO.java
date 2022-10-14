package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
public class ReportDTO extends AuthQueryResponseCommonExt implements Serializable {

    private static final long serialVersionUID = 4235565595934721788L;

    @ApiModelProperty(value = "报告ID")
    private Long id;

    @ApiModelProperty(value = "消耗流量")
    private BigDecimal amount;


    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "并发数")
    private Integer concurrent;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "压测结果")
    private Integer conclusion;

    @ApiModelProperty(value = "压测总计时")
    private String totalTime;

}
