package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-16 13:55
 */

@Data
@ApiModel(value = "机器详情记录请求模型")
public class PressureMachineLogQueryRequest implements Serializable {
    private static final long serialVersionUID = -7713364052692042766L;

    @ApiModelProperty(value = "查询时间")
    @NotNull
    private String queryTime;

    @ApiModelProperty(value = "机器ip")
    @NotNull
    private Long machineId;

}
