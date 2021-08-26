package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/8 3:05 下午
 */
@Data
@ApiModel("入参类 --> 应用中间件比对入参类")
public class CompareApplicationMiddlewareRequest {

    @ApiModelProperty(value = "应用id", required = true)
    @NotNull(message = "应用id" + AppConstants.MUST_BE_NOT_NULL)
    private Long applicationId;

}
