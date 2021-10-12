package io.shulie.takin.web.diff.cloud.impl.scenemange;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import io.shulie.takin.cloud.open.req.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Component
public class SceneManageApiImpl implements SceneManageApi {

    @Resource(type = CloudSceneApi.class)
    private CloudSceneApi cloudSceneApi;

    @Resource(type = CloudEngineApi.class)
    private CloudEngineApi cloudEngineApi;

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
        try {
            return ResponseResult.success(cloudSceneApi.getSceneList(sceneManageQueryReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
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
}
