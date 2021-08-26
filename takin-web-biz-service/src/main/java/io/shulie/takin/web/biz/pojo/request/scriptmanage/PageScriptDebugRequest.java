package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询调试记录入参
 *
 * @author liuchuan
 * @date 2021/5/12 10:04 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参类 --> 分页查询调试记录入参类")
public class PageScriptDebugRequest extends PageBaseDTO {

    /**
     * 脚本发布id
     */
    @ApiModelProperty(value = "脚本发布id", required = true)
    @NotNull(message = "脚本发布id" + AppConstants.MUST_BE_NOT_NULL)
    private Long scriptDeployId;

}
