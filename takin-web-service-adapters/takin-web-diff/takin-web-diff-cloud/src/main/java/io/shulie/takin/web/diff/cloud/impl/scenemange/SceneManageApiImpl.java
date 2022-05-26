package io.shulie.takin.web.diff.cloud.impl.scenemange;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.engine.CloudEngineApi;
import io.shulie.takin.adapter.api.entrypoint.process.ProcessApi;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneIpNumReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Component
public class SceneManageApiImpl implements SceneManageApi {

    @Resource(type = CloudSceneManageApi.class)
    private CloudSceneManageApi cloudSceneApi;

    @Resource(type = CloudEngineApi.class)
    private CloudEngineApi cloudEngineApi;
    @Resource(type = ProcessApi.class)
    private ProcessApi processApi;

    @Override
    public ResponseResult<List<SceneManageListResp>> querySceneByStatus(SceneManageQueryReq sceneManageQueryReq) {
        //return cloudSceneApi.querySceneByStatus(sceneManageQueryReq);
        // TODO
        return null;
    }

    @Override
    public ResponseResult<Object> updateSceneFileByScriptId(CloudUpdateSceneFileRequest updateSceneFileRequest) {
        try {
            cloudSceneApi.updateSceneFileByScriptId(updateSceneFileRequest);
            return ResponseResult.success(true);
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<Long> saveScene(SceneManageWrapperReq sceneManageWrapperReq) {
        try {
            return ResponseResult.success(cloudSceneApi.saveScene(sceneManageWrapperReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<String> updateScene(SceneManageWrapperReq req) {
        try {
            return ResponseResult.success(cloudSceneApi.updateScene(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<String> deleteScene(SceneManageDeleteReq sceneManageDeleteReq) {
        try {
            return ResponseResult.success(cloudSceneApi.deleteScene(sceneManageDeleteReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<SceneManageWrapperResp> getSceneDetail(SceneManageIdReq sceneManageIdVO) {
        try {
            return ResponseResult.success(cloudSceneApi.getSceneDetail(sceneManageIdVO));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq sceneManageQueryReq) {
        return cloudSceneApi.getSceneList(sceneManageQueryReq);
    }

    @Override
    public ResponseResult<BigDecimal> calcFlow(SceneManageWrapperReq sceneManageWrapperReq) {
        try {
            return ResponseResult.success(cloudSceneApi.calcFlow(sceneManageWrapperReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<StrategyResp> getIpNum(SceneIpNumReq sceneIpNumReq) {
        try {
            return ResponseResult.success(cloudSceneApi.getIpNum(sceneIpNumReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req) {
        try {
            return ResponseResult.success(cloudSceneApi.queryByIds(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneManageList(ContextExt traceContextExt) {
        try {
            return ResponseResult.success(cloudSceneApi.getSceneManageList(traceContextExt));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    /**
     * 获取支持的jmeter插件列表
     *
     * @return -
     */
    @Override
    public ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> listEnginePlugins(EnginePluginFetchWrapperReq wrapperReq) {
        try {
            return ResponseResult.success(cloudEngineApi.listEnginePlugins(wrapperReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    /**
     * 获取支持的jmeter插件详情
     *
     * @return -
     */
    @Override
    public ResponseResult<EnginePluginDetailResp> getEnginePluginDetails(EnginePluginDetailsWrapperReq wrapperReq) {
        try {
            return ResponseResult.success(cloudEngineApi.getEnginePluginDetails(wrapperReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<ScriptCheckResp> checkAndUpdateScript(ScriptCheckAndUpdateReq scriptCheckAndUpdateReq) {
        try {
            return ResponseResult.success(cloudSceneApi.checkAndUpdateScript(scriptCheckAndUpdateReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public List<ScriptNode> scriptAnalyze(ScriptAnalyzeRequest request) {
        WebPluginUtils.fillCloudUserData(request);
        return processApi.scriptAnalyze(request);
    }
}
