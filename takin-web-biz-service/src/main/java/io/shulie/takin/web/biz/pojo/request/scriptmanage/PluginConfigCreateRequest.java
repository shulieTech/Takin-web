package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/20 4:39 下午
 */
@Data
public class PluginConfigCreateRequest {
    @ApiModelProperty(name = "type", value = "插件类型")
    private String type;

    @ApiModelProperty(name = "name", value = "插件名称")
    private String name;

    @ApiModelProperty(name = "version", value = "插件版本")
    private String version;
}
