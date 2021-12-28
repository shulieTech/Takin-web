package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:31:15
 */
@ApiModel("入参类-入参")
@Data
public class ApplicationPluginUpgradeRefRequest {

    @ApiModelProperty("id")
    private Long id;

}
