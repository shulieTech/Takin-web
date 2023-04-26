package com.pamirs.takin.entity.domain.dto.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 瓶颈接口DTO
 *
 * @author qianshui
 * @date 2020/7/22 下午2:31
 */
@ApiModel
@Data
public class BottleneckInterfaceLtDTO implements Serializable {

    @ApiModelProperty(value = "排名")
    private Integer rank;

    @ApiModelProperty(value = "应用名称")
    private String applicationName;

    @ApiModelProperty(value = "接口")
    private String interfaceName;

    @ApiModelProperty(value = "平均Tps")
    private BigDecimal avgTps;

    @ApiModelProperty(value = "最小Tps")
    private BigDecimal minTps;

    @ApiModelProperty(value = "最大Tps")
    private BigDecimal maxTps;

    @ApiModelProperty(value = "平均rt")
    private BigDecimal avgRt;

    @ApiModelProperty(value = "最小rt")
    private BigDecimal minRt;

    @ApiModelProperty(value = "最大rt")
    private BigDecimal maxRt;

    @ApiModelProperty(value = "成功率")
    private BigDecimal successRate;

}
