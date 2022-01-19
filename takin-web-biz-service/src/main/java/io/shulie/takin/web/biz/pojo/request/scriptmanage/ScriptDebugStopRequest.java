package io.shulie.takin.web.biz.pojo.request.scriptmanage;

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
@ApiModel("入参类 --> 脚本调试停止入参类")
public class ScriptDebugStopRequest {

    @ApiModelProperty(value = "脚本实例id", required = true)
    @NotNull(message = "脚本实例id" + AppConstants.MUST_BE_NOT_NULL)
    private Long scriptDeployId;

}