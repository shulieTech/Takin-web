package com.pamirs.takin.cloud.entity.domain.dto.strategy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/9 下午8:33
 */
@Data
@ApiModel("分配策略详情")
public class StrategyConfigDetailDTO {

    @ApiModelProperty(value = "策略名称")
    private String strategyName;

    @ApiModelProperty(value = "策略配置")
    private String strategyConfig;
}
