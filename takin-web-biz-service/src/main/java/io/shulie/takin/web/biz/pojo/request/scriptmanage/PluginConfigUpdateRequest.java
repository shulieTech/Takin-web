package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/20 5:42 下午
 */
@Data
public class PluginConfigUpdateRequest extends AbstractEntry {
    @ApiModelProperty(name = "type", value = "插件类型")
    private String type;

    @ApiModelProperty(name = "name", value = "插件名称")
    private String name;

    @ApiModelProperty(name = "version", value = "插件版本")
    private String version;
}
