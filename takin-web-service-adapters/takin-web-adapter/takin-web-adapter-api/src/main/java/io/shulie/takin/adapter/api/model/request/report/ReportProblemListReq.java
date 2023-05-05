package io.shulie.takin.adapter.api.model.request.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("问题诊断-业务活动-入参")
public class ReportProblemListReq implements Serializable {

    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "服务名称", required = true)
    @NotNull(message = "服务名称不能为空")
    private String serviceName;

}
