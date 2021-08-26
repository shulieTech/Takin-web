package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 压测报告；统计返回
 *
 * @author qianshui
 * @date 2020/7/22 下午2:19
 */
@ApiModel
@Data
public class ReportCountDTO implements Serializable {

    private static final long serialVersionUID = 8928035842416997931L;

    /**
     * 瓶颈接口数
     */
    @ApiModelProperty(value = "瓶颈接口数")
    private Integer bottleneckInterfaceCount;

    /**
     * 风险机器数
     */
    @ApiModelProperty(value = "风险机器数")
    private Integer riskMachineCount;

    /**
     * 业务活动数
     */
    @ApiModelProperty(value = "业务活动数")
    private Integer businessActivityCount;

    /**
     * 未达标业务活动数
     */
    @ApiModelProperty(value = "未达标业务活动数")
    private Integer notpassBusinessActivityCount;

    /**
     * 应用数
     */
    @ApiModelProperty(value = "应用数")
    private Integer applicationCount;

    /**
     * 机器数
     */
    @ApiModelProperty(value = "机器数")
    private Integer machineCount;

    /**
     * 告警数
     */
    @ApiModelProperty(value = "告警数")
    private Integer warnCount;

}
