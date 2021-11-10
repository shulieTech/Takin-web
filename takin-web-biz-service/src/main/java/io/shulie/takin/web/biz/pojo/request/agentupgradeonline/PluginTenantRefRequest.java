package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 插件版本库(PluginTenantRef)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-11-10 17:52:43
 */
@ApiModel("入参类-入参")
@Data
public class PluginTenantRefRequest {

    @ApiModelProperty("id")
    private Long id;

}
