package io.shulie.takin.adapter.api.model.request.scenetask;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xr.l
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTryRunTaskCheckReq extends ContextExt {

    @ApiModelProperty(value = "场景Id")
    private Long sceneId;

    @ApiModelProperty(value = "报告Id")
    private Long reportId;

}
