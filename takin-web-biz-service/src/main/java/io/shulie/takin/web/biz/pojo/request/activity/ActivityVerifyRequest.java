package io.shulie.takin.web.biz.pojo.request.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("业务活动流量验证")
public class ActivityVerifyRequest {

    @ApiModelProperty("业务活动ID")
    private Long activityId;

    @ApiModelProperty("脚本ID")
    private Long scriptId;
}
