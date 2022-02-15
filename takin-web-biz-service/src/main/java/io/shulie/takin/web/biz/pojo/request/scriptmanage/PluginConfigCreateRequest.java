package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fanxx
 * @date 2021/1/20 4:39 下午
 */
@Data
public class PluginConfigCreateRequest {
    @ApiModelProperty(name = "type", value = "插件类型")
    private String type;

    @ApiModelProperty(name = "name", value = "插件名称-实际上是用id")
    private String name;

    @ApiModelProperty(name = "version", value = "插件版本")
    private String version;
}
