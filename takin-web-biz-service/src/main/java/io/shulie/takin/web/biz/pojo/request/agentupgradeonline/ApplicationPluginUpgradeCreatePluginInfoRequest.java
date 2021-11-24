package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: 南风
 * @Date: 2021/11/23 4:44 下午
 */
@Data
public class ApplicationPluginUpgradeCreatePluginInfoRequest {

    @ApiModelProperty(value = "插件Id")
    private Long pluginId;

    /**
     * 插件名称
     */
    @ApiModelProperty(value = "插件名称")
    private String pluginName;

    /**
     * 插件类型
     *
     * @see io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum
     */
    @ApiModelProperty(value = "插件类型")
    private Integer pluginType;



    /**
     * 插件版本
     */
    @ApiModelProperty(value = "插件版本")
    private String version;

}
