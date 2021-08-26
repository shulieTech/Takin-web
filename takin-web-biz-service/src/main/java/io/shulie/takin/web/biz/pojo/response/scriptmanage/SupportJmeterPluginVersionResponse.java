package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import lombok.Data;

import java.util.List;


import io.swagger.annotations.ApiModelProperty;

/**
 * @author fanxx
 * @date 2021/1/18 7:57 下午
 */
@Data
public class SupportJmeterPluginVersionResponse {

    @ApiModelProperty(name = "pluginId", value = "插件id")
    private Long pluginId;

    @ApiModelProperty(name = "versionList", value = "插件版本列表")
    private List<String> versionList;
}
