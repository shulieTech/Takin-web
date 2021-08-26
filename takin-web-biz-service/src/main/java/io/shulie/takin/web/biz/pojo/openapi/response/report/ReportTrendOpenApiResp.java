package io.shulie.takin.web.biz.pojo.openapi.response.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel
public class ReportTrendOpenApiResp implements Serializable {

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
}
