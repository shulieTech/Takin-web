package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 瓶颈接口DTO
 *
 * @author qianshui
 * @date 2020/7/22 下午2:31
 */
@ApiModel
@Data
public class BottleneckInterfaceDTO implements Serializable {

    private static final long serialVersionUID = -7218988438361002963L;

    /**
     * 排名
     */
    @ApiModelProperty(value = "排名")
    private Integer rank;

    /**
     * 应用
     */
    @ApiModelProperty(value = "应用")
    private String applicationName;

    /**
     * 接口
     */
    @ApiModelProperty(value = "接口")
    private String interfaceName;

    /**
     * tps
     */
    @ApiModelProperty(value = "tps")
    private BigDecimal tps;

    /**
     * rt
     */
    @ApiModelProperty(value = "rt")
    private BigDecimal rt;

    /**
     * 成功率
     */
    @ApiModelProperty(value = "成功率")
    private BigDecimal successRate;

}
