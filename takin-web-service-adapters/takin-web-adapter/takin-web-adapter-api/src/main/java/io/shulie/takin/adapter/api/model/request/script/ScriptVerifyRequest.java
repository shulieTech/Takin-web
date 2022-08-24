package io.shulie.takin.adapter.api.model.request.script;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptVerifyRequest extends ContextExt {

    private String attach;
    private String callbackUrl;
    private String watchmanId;
    private List<String> watchmanIdList;
    // 脚本文件路径
    private String scriptPath;
    private List<String> pluginPath;
    private List<String> dataFilePath;
    private List<String> attachmentsPath;
}
