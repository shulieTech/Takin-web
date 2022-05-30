package io.shulie.takin.cloud.data.result.scenemanage;

import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/18 上午10:59
 */
@Data
public class SceneSlaRefResult {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleBean rule;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "触发事件")
    private String event;
}
