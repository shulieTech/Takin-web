package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/18 上午10:58
 */
@Data
public class RuleVO implements Serializable {

    private static final long serialVersionUID = 1789327058040467753L;

    @ApiModelProperty(value = "指标类型")
    private Integer indexInfo;

    @ApiModelProperty(value = "条件")
    private Integer condition;

    @ApiModelProperty(value = "满足值")
    private BigDecimal during;

    @ApiModelProperty(value = "连续触发次数")
    private Integer times;
}
