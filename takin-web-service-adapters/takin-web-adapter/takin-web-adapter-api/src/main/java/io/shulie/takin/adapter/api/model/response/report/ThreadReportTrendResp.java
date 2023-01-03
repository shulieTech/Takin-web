package io.shulie.takin.adapter.api.model.response.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ThreadReportTrendResp implements Serializable {

    @ApiModelProperty(value = "并发数")
    private List<String> concurrent;

    @ApiModelProperty(value = "tps")
    private List<String> tps;

    @ApiModelProperty(value = "rt")
    private List<String> rt;
}
