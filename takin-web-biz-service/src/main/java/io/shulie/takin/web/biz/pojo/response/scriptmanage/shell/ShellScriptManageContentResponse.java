package io.shulie.takin.web.biz.pojo.response.scriptmanage.shell;

import java.io.Serializable;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ShellScriptManageContentResponse implements Serializable {
    private static final long serialVersionUID = -3681184638943613401L;

    @ApiParam(value = "脚本内容")
    private String content;

    @ApiParam(value = "脚本版本")
    private Integer ScriptVersion;

    @ApiParam(value = "描述")
    private String description;

    @ApiParam(value = "脚本实例id")
    private Long scriptManageDeployId;
}
