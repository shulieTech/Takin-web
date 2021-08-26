package io.shulie.takin.web.biz.pojo.request.scriptmanage.shell;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ShellScriptManageDeleteRequest implements Serializable {
    private static final long serialVersionUID = 9207364066819470901L;

    @JsonProperty("scriptId")
    @ApiModelProperty(name = "scriptId", value = "脚本id")
    @NotNull(message = "脚本id不能为空")
    private Long scriptId;
}
