package com.pamirs.takin.cloud.entity.domain.dto.schedule;

import java.math.BigDecimal;

import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:06
 */
@Data
@ApiModel(description = "列表查询出参")
public class ScheduleRecordDTO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "pod数")
    private Integer podNum;

    @ApiModelProperty(value = "pod种类")
    private String podClass;

    private transient Integer statusInt;

    @ApiModelProperty(value = "状态")
    private EnumResult status;

    @ApiModelProperty(value = "cpu核数")
    private BigDecimal cpuCoreNum;

    @ApiModelProperty(value = "内存大小")
    private BigDecimal memorySize;

    @ApiModelProperty(value = "调度时间")
    private String createTime;
}
