package io.shulie.takin.adapter.cloud.impl.process;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.service.script.ScriptAnalyzeService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.adapter.api.entrypoint.process.ProcessApi;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 新业务流程相关接口
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ProcessApiImpl implements ProcessApi {

    @Resource
    private ScriptAnalyzeService scriptAnalyzeService;

    /**
     * 脚本解析
     *
     * @param request 入参
     * @return 脚本解析结果
     */
    @Override
    public List<ScriptNode> scriptAnalyze(ScriptAnalyzeRequest request) {
        if (StringUtils.isBlank(request.getScriptFile())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_PARAMS_ERROR, "请提供脚本文件完整的路径和名称");
        }
        File file = new File(request.getScriptFile());
        if (!file.exists() || !file.isFile()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_FILE_NOT_EXISTS, "请检测脚本文件是否存在");
        }
        List<ScriptNode> nodes = scriptAnalyzeService.buildNodeTree(request.getScriptFile());
        if (CollectionUtils.isEmpty(nodes)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_FAILED, "请检测脚本内容");
        }
        return nodes;
    }
}
