package com.pamirs.takin.entity.domain.dto.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("请求报文")
public class ReportMessageDetailDTO implements Serializable {

    @ApiModelProperty("traceId")
    private String traceId;

    @ApiModelProperty("请求耗时，单位ms")
    private String cost;

    @ApiModelProperty("请求头")
    private String requestHeader;

    @ApiModelProperty("请求体")
    private String requestBody;

    @ApiModelProperty("响应头")
    private String responseHeader;

    @ApiModelProperty("响应体")
    private String responseBody;

}
