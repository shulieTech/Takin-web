package io.shulie.takin.web.biz.pojo.request.config;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/8 3:05 下午
 */
@Data
@ApiModel("入参类 --> 修改服务配置入参类")
public class UpdateConfigServerRequest {

    @ApiModelProperty(value = "配置的 key", required = true)
    @NotBlank(message = "配置的 key" + AppConstants.MUST_BE_NOT_NULL)
    private String key;

    @ApiModelProperty(value = "配置的值", required = true)
    @NotBlank(message = "配置的值" + AppConstants.MUST_BE_NOT_NULL)
    private String value;

}
