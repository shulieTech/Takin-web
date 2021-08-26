package com.pamirs.takin.entity.domain.entity.linkmanage.statistics;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/10 01:03
 */
@Data
@ApiModel(value = "StatisticsQueryVo", description = "StatisticsQueryVo")
public class StatisticsQueryVo extends PagingDevice {
    @ApiModelProperty(name = "middleWareType", value = "中间件类型")
    private String middleWareType;
    @ApiModelProperty(name = "businessProcess", value = "业务流程id")
    private Long businessProcess;
    @ApiModelProperty(name = "systemProcess", value = "系统流程id")
    private Long systemProcess;
}
