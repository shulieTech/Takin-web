package io.shulie.takin.adapter.api.model.request.scenetask;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liyuanba
 * @date 2021/11/26 11:00 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskStopReq extends ContextExt {
    @NotNull
    @ApiModelProperty(value = "任务ID")
    private Long reportId;

    @ApiModelProperty(value = "是否需要生成最终报告")
    private boolean finishReport = true;
}
