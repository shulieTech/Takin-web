package io.shulie.takin.adapter.api.model.request.scenemanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.SlaBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 8:06 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageIdReq extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "报告id")
    private Long reportId;

    @ApiModelProperty(value = "sla触发")
    private SlaBean slaBean;
}
