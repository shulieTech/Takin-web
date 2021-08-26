package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用对象
 *
 * @author qianshui
 * @date 2020/7/22 下午3:14
 */

@ApiModel
@Data
public class ApplicationDTO implements Serializable {

    private static final long serialVersionUID = 2965776774948266201L;

    @ApiModelProperty(value = "应用")
    private String applicationName;

    @ApiModelProperty(value = "风险机器")
    private Integer riskCount;

    @ApiModelProperty(value = "总机器")
    private Integer totalCount;

}
