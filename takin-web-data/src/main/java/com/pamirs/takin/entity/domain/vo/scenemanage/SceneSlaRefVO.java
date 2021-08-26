package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/17 下午4:43
 */
@Data
public class SceneSlaRefVO implements Serializable {

    private static final long serialVersionUID = 4747478435828708203L;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "适用对象")
    private String[] businessActivity;

    @ApiModelProperty(value = "规则")
    private RuleVO rule;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;
}
