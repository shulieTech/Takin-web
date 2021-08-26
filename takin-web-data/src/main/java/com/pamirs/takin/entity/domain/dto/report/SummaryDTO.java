package com.pamirs.takin.entity.domain.dto.report;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
public class SummaryDTO {

    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "趋势数据")
    private List data;

}
