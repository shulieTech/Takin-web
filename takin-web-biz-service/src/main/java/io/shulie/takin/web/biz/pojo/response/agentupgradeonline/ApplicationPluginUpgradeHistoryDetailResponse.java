package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: 南风
 * @Date: 2021/11/23
 */
@Data
public class ApplicationPluginUpgradeHistoryDetailResponse {

    @ApiModelProperty(value = "插件名称")
    private String pluginName;

    @ApiModelProperty(value = "插件版本")
    private String version;


    public ApplicationPluginUpgradeHistoryDetailResponse(String pluginName, String version) {
        this.pluginName = pluginName;
        this.version = version;
    }

    public ApplicationPluginUpgradeHistoryDetailResponse() {
    }
}
