package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/18 5:22 下午
 */
@Data
public class SupportJmeterPluginVersionRequest {

    @ApiModelProperty(name = "pluginId", value = "插件id")
    @NotNull
    private Long pluginId;
}
