package io.shulie.takin.web.biz.pojo.request.agent;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/30 4:46 下午
 */
@Data
@ApiModel("入参类-推送应用中间件列表")
public class PushMiddlewareRequest implements AppConstants {

    @ApiModelProperty(value = "应用名称", required = true)
    @NotBlank(message = "应用名称" + MUST_NOT_BE_NULL)
    private String applicationName;

    @ApiModelProperty(value = "中间件列表", required = true)
    @NotEmpty(message = "中间件列表" + MUST_NOT_BE_EMPTY)
    private List<PushMiddlewareListRequest> middlewareList;

}
