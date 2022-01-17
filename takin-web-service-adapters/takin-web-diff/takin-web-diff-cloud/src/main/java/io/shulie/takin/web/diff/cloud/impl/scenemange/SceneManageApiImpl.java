package io.shulie.takin.web.diff.cloud.impl.scenemange;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.open.api.engine.CloudEngineApi;
import io.shulie.takin.cloud.open.api.scenemanage.CloudSceneApi;
import io.shulie.takin.cloud.open.req.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.open.req.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.cloud.open.req.scenemanage.SceneIpNumReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Component
public class SceneManageApiImpl implements SceneManageApi {

    @Autowired
    private CloudSceneApi cloudSceneApi;

    @Autowired
    private CloudEngineApi cloudEngineApi;

    @Override
    public ResponseResult<List<SceneManageListResp>> querySceneByStatus(SceneManageQueryReq sceneManageQueryReq) {
        return cloudSceneApi.querySceneByStatus(sceneManageQueryReq);
    }

    @Override
    public ResponseResult<Object> updateSceneFileByScriptId(CloudUpdateSceneFileRequest updateSceneFileRequest) {
        return (ResponseResult<Object>)cloudSceneApi.updateSceneFileByScriptId(updateSceneFileRequest);
    }

    @Override
    public ResponseResult<Long> saveScene(SceneManageWrapperReq sceneManageWrapperReq) {
        return cloudSceneApi.saveScene(sceneManageWrapperReq);
    }

    @Override
    public ResponseResult<String> updateScene(SceneManageWrapperReq req) {
        return cloudSceneApi.updateScene(req);
    }

    @Override
    public ResponseResult deleteScene(SceneManageDeleteReq sceneManageDeleteReq) {
        return cloudSceneApi.deleteScene(sceneManageDeleteReq);
    }

    @Override
    public ResponseResult<SceneManageWrapperResp> getSceneDetail(SceneManageIdReq sceneManageIdVO) {
        return cloudSceneApi.getSceneDetail(sceneManageIdVO);
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq sceneManageQueryReq) {
        return cloudSceneApi.getSceneList(sceneManageQueryReq);
    }

    @Override
    public ResponseResult<BigDecimal> calcFlow(SceneManageWrapperReq sceneManageWrapperReq) {
        return cloudSceneApi.calcFlow(sceneManageWrapperReq);
    }

    @Override
    public ResponseResult<StrategyResp> getIpNum(SceneIpNumReq sceneIpNumReq) {
        return cloudSceneApi.getIpNum(sceneIpNumReq);
    }


    @Override
    public ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req) {
        return cloudSceneApi.queryByIds(req);
    }


    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneManageList(CloudUserCommonRequestExt requestExt) {
        return cloudSceneApi.getSceneManageList(requestExt);
    }

    /**
     * 获取支持的jmeter插件列表
     *
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> listEnginePlugins(EnginePluginFetchWrapperReq wrapperReq) {
        return cloudEngineApi.listEnginePlugins(wrapperReq);
    }

    /**
     * 获取支持的jmeter插件详情
     *
     * @return
     */
    @Override
    public ResponseResult<EnginePluginDetailResp> getEnginePluginDetails(EnginePluginDetailsWrapperReq wrapperReq) {
        WebPluginUtils.fillCloudUserData(wrapperReq);
        return cloudEngineApi.getEnginePluginDetails(wrapperReq);
    }

    @Override
    public ResponseResult<ScriptCheckResp> checkAndUpdateScript(ScriptCheckAndUpdateReq scriptCheckAndUpdateReq) {
        WebPluginUtils.fillCloudUserData(scriptCheckAndUpdateReq);
        return cloudSceneApi.checkAndUpdateScript(scriptCheckAndUpdateReq);
    }

    @Override
    public ResponseResult<List<ScriptNode>> scriptAnalyze(ScriptAnalyzeRequest request) {
        WebPluginUtils.fillCloudUserData(request);
        return cloudSceneApi.scriptAnalyze(request);
    }
}
