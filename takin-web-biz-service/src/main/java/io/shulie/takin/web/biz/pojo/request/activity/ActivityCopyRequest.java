package io.shulie.takin.web.biz.pojo.request.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("业务活动复制对象")
public class ActivityCopyRequest implements Serializable {

    @NotNull
    @ApiModelProperty("业务活动ID")
    private Long activityId;
}
