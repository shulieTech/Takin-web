package io.shulie.takin.web.biz.pojo.request.scriptmanage.shell;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/14 12:49 下午
 */
@Data
public class ShellScriptManageRequest {
    /**
     * 名称
     */
    @JsonProperty("scriptName")
    @ApiModelProperty(name = "scriptName", value = "脚本名")
    private String name;

    /**
     * 描述
     */
    @JsonProperty("description")
    @ApiModelProperty(name = "description", value = "脚本描述")
    private String description;

    /**
     * shell脚本内容
     */
    @JsonProperty("content")
    @ApiModelProperty(name = "content", value = "脚本内容")
    private String content;
}
