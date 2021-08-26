package com.pamirs.takin.entity.domain.dto.schedule;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/9 下午2:06
 */
@Data
@ApiModel(description = "列表查询出参")
public class ScheduleRecordDTO implements Serializable {
    private static final long serialVersionUID = -9035230941808348639L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "pod数")
    private Integer podNum;

    @ApiModelProperty(value = "pod种类")
    private String podClass;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "cpu核数")
    private Integer cpuCoreNum;

    @ApiModelProperty(value = "内存大小")
    private BigDecimal memorySize;

    @ApiModelProperty(value = "调度时间")
    private String createTime;
}
