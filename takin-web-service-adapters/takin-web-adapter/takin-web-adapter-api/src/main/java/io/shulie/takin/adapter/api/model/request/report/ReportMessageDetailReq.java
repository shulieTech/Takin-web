package io.shulie.takin.adapter.api.model.request.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReportMessageDetailReq extends ReportMessageCodeReq{

    @ApiModelProperty(value = "状态码", required = true)
    private String statusCode;

    @ApiModelProperty("自动通过状态码转换")
    private String resultCode;
}
