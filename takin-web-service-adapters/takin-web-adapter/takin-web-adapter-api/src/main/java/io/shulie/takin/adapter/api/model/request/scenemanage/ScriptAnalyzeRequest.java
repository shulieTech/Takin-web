package io.shulie.takin.adapter.api.model.request.scenemanage;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liyuanba
 * @date 2021/10/26 1:51 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptAnalyzeRequest extends ContextExt {
    @ApiModelProperty(name = "scriptFile", value = "脚本文件")
    @NotBlank(message = "脚本文件不能为空")
    private String scriptFile;
}
