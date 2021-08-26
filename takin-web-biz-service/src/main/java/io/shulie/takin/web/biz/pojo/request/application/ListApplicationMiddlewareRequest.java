package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用中间件(ApplicationMiddleware)controller 应用中间件列表入参
 *
 * @author liuchuan
 * @date 2021-06-30 16:11:28
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("入参类-应用中间件列表入参")
@Data
public class ListApplicationMiddlewareRequest extends PageBaseDTO {

    @ApiModelProperty(value = "应用id", required = true)
    @NotNull(message = "应用id" + AppConstants.MUST_NOT_BE_NULL)
    private Long applicationId;

    @ApiModelProperty("搜索关键字, artifactId 或者 groupId")
    private String keywords;

    @ApiModelProperty("状态, 3 已支持, 2 未支持, 4 无需支持, 1 未知, 0 无")
    private Integer status;

}
