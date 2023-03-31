package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("新增业务流程-串联链路-API-定时器")
public class PtsApiTimerRequest implements Serializable {
    @ApiModelProperty(value = "线程延迟-ms")
    private String delay;
}
