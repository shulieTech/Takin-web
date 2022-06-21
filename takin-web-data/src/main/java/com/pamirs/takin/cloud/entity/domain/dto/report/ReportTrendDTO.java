package com.pamirs.takin.cloud.entity.domain.dto.report;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-20
 */
@Data
@ApiModel
public class ReportTrendDTO {

    @ApiModelProperty(value = "刻度，时间")
    private List<String> time;

    @ApiModelProperty(value = "tps")
    private List<String> tps;

    @ApiModelProperty(value = "rt")
    private List<String> rt;

    @ApiModelProperty(value = "成功率")
    private List<String> successRate;

    @ApiModelProperty(value = "sa")
    private List<String> sa;

    @ApiModelProperty(value = "并发数")
    private List<String> concurrent;
}
