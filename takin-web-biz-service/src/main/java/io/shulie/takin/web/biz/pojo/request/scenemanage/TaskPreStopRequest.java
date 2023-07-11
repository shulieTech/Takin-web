package io.shulie.takin.web.biz.pojo.request.scenemanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/13 4:44 下午
 */
@Data
@ApiModel("入参类 --> 压测启动中停止入参类")
public class TaskPreStopRequest {

    @ApiModelProperty(value = "场景id", required = true)
    @NotNull(message = "场景id" + AppConstants.MUST_BE_NOT_NULL)
    private Long sceneId;

}