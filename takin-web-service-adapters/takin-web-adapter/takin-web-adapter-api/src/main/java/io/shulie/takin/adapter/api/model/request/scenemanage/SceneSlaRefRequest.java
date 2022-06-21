package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shulie
 */
@Data
public class SceneSlaRefRequest {

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;
}
