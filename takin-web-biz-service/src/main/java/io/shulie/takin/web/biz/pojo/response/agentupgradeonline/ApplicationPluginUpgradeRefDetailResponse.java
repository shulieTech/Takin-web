package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)controller 详情响应类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:31:14
 */
@ApiModel("出参类-详情出参")
@Data
public class ApplicationPluginUpgradeRefDetailResponse {

    @ApiModelProperty("id")
    private Long id;

}
