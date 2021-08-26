package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/18 5:29 下午
 */
@Data
public class SinglePluginRenderResponse {
    @ApiModelProperty(name = "label", value = "插件名称")
    private String label;

    @ApiModelProperty(name = "value", value = "插件id")
    private Long value;
}
