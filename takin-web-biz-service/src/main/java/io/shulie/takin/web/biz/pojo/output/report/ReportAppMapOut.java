package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "ReportAppMapOut", description = "应用趋势图")
public class ReportAppMapOut {

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "时间和请求数")
    private Map<String, Integer> timeAndRequestMap;

    @ApiModelProperty(value = "tps数组")
    private Double tps[];

    @ApiModelProperty(value = "rt数组")
    private Double rt[];

    @ApiModelProperty(value = "成功率数组")
    private Double successRate[];

    @ApiModelProperty(value = "请求数数组")
    private Integer totalRequest[];

}
