package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 风险机器 应用 数量 汇总
 *
 * @author qianshui
 * @date 2020/7/22 下午2:42
 */
@ApiModel
@Data
public class RiskApplicationCountDTO implements Serializable {

    private static final long serialVersionUID = 4060772976299354894L;

    /**
     * 应用数量
     */
    @ApiModelProperty(value = "应用数量")
    private Integer applicationCount;

    /**
     * 机器总数
     */
    @ApiModelProperty(value = "机器总数")
    private Integer machineCount;

    /**
     * 应用列表
     */
    @ApiModelProperty(value = "应用列表")
    private List<ApplicationDTO> applicationList;
}
