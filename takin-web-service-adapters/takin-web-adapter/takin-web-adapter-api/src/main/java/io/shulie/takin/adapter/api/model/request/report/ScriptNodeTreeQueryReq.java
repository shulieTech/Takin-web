package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class ScriptNodeTreeQueryReq extends ContextExt {

    @ApiModelProperty(name = "sceneId", value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(name = "reportId", value = "报告ID")
    private Long reportId;
}
