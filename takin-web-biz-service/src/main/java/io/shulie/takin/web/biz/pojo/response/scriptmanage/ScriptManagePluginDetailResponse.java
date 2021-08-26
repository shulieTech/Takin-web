package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanxx
 * @date 2021/1/18 5:33 下午
 */
@Data
public class ScriptManagePluginDetailResponse {

    @ApiModelProperty(name = "pluginInfo", value = "插件详情列表")
    private List<SinglePluginConfigResponse> pluginInfo;
}
