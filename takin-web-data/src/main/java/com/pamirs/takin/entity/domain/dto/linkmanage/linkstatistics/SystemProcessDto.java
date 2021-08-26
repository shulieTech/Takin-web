package com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/7 00:31
 */
@Data
@ApiModel(value = "SystemProcessDto", description = "SystemProcessDto")
public class SystemProcessDto implements Serializable {
    @ApiModelProperty(name = "month", value = "月份")
    private String month;
    @ApiModelProperty(name = "cover", value = "数量")
    private String cover;
}
