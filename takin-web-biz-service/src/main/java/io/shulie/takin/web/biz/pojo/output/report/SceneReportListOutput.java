package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("压测场景对应报告列表")
public class SceneReportListOutput implements Serializable {

    @ApiModelProperty(value = "压测报告id")
    private Long reportId;

    @ApiModelProperty(value = "最大并发数")
    private Integer maxConcurrent;

    @ApiModelProperty(value = "压测开始时间")
    private String startTime;
}
