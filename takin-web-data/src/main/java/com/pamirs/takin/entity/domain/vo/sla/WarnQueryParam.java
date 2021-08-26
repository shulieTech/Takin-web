package com.pamirs.takin.entity.domain.vo.sla;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModelProperty;
import com.pamirs.takin.entity.domain.PagingDevice;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnQueryParam extends PagingDevice {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "SLA ID")
    private Long slaId;

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;
}
