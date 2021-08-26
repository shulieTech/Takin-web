package com.pamirs.takin.entity.domain.dto.strategy;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/9 下午8:33
 */
@Data
@ApiModel("分配策略详情")
public class StrategyConfigDetailDTO implements Serializable {

    private static final long serialVersionUID = 6612363903300276594L;

    @ApiModelProperty(value = "策略名称")
    private String strategyName;

    @ApiModelProperty(value = "策略配置")
    private String strategyConfig;
}
