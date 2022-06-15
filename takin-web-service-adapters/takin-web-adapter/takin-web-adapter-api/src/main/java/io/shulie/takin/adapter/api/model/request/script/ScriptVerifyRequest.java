package io.shulie.takin.adapter.api.model.request.script;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptVerifyRequest extends ContextExt {

    // 脚本文件路径
    private String scriptPath;
    // 插件路径，多个按逗号分隔
    private String pluginPaths;
    // 数据文件路径，多个按逗号分隔
    private String csvPaths;
    // 附件文件路径，多个按逗号分隔
    private String attachments;
}
