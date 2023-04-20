package com.pamirs.takin.entity.domain.dto.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 风险机器列表
 *
 * @author qianshui
 * @date 2020/7/22 下午2:57
 */
@ApiModel
@Data
public class RiskMachineLtDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "实例ID")
    private String agentId;

    @ApiModelProperty(value = "风险描述")
    private String riskContent;

}
