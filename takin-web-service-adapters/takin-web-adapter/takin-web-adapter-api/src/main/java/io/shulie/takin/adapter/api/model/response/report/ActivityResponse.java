package io.shulie.takin.adapter.api.model.response.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author 张天赐
 */
@Data
public class ActivityResponse {
    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;
}
