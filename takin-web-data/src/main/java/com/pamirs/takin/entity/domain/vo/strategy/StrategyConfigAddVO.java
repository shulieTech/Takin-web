package com.pamirs.takin.entity.domain.vo.strategy;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/9 下午3:21
 */
@Data
@ApiModel(description = "配置策略信息")
public class StrategyConfigAddVO implements Serializable {

    private static final long serialVersionUID = -7643529208229180591L;

    @NotNull
    @ApiModelProperty(value = "调度策略名称")
    private String strategyName;

    @NotNull
    @ApiModelProperty(value = "调度策略配置")
    private String strategyConfig;
}
