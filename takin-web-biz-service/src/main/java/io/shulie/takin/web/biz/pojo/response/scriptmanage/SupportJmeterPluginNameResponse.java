package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import lombok.Data;

import java.util.List;


import io.swagger.annotations.ApiModelProperty;

/**
 * @author fanxx
 * @date 2021/1/18 5:27 下午
 */
@Data
public class SupportJmeterPluginNameResponse {

    @ApiModelProperty(name = "type", value = "插件类型")
    private String type;

    @ApiModelProperty(name = "pluginResponseList", value = "插件列表")
    private List<SinglePluginRenderResponse> singlePluginRenderResponseList;
}
