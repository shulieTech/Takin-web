package com.pamirs.takin.cloud.entity.domain.dto.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 风险机器列表
 *
 * @author qianshui
 * @date 2020/7/22 下午2:57
 */
@ApiModel
@Data
public class RiskMacheineDTO {

    @ApiModelProperty(value = "机器")
    private String machineIp;

    @ApiModelProperty(value = "风险")
    private String riskContent;

}
