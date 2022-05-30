package com.pamirs.takin.cloud.entity.domain.dto.report;

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
public class ApplicationDTO {

    @ApiModelProperty(value = "应用")
    private String applicationName;

    @ApiModelProperty(value = "风险机器")
    private Integer riskCount;

    @ApiModelProperty(value = "总机器")
    private Integer totalCount;

}
