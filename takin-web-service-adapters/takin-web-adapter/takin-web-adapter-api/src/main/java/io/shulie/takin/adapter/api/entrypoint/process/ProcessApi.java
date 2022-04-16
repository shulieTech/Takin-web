package io.shulie.takin.adapter.api.entrypoint.process;

import java.util.List;

import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;

/**
 * 新业务流程相关接口
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@SuppressWarnings("unused")
public interface ProcessApi {
    /**
     * 脚本解析
     *
     * @param request 入参
     * @return 脚本解析结果
     */
    List<ScriptNode> scriptAnalyze(ScriptAnalyzeRequest request);
}
