package com.pamirs.takin.entity.domain.dto.strategy;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/9 下午2:06
 */
@Data
@ApiModel(description = "分配策略列表")
public class StrategyConfigDTO implements Serializable {

    private static final long serialVersionUID = -8740307347149572470L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "配置名称")
    private String strategyName;

    @ApiModelProperty(value = "并发数")
    private String threadNum;

    @ApiModelProperty(value = "cpu")
    private String cpuNum;

    @ApiModelProperty(value = "内存")
    private String memorySize;

    @ApiModelProperty(value = "最后修改时间")
    private String updateTime;
}
