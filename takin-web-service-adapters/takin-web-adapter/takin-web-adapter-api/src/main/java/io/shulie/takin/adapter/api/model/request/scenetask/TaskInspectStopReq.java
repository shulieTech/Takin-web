package io.shulie.takin.adapter.api.model.request.scenetask;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2021/4/14 4:36 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskInspectStopReq extends ContextExt {

    @ApiModelProperty(value = "场景Id")
    @NotNull
    private Long sceneId;
}
