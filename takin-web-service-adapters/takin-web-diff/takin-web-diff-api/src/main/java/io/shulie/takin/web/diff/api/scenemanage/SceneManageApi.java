package io.shulie.takin.web.diff.api.scenemanage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.validation.annotation.Validated;

/**
 * @author 无涯
 * @date 2020/10/27 4:22 下午
 */
public interface SceneManageApi {

    /**
     * 通过场景状态查询场景列表
     *
     * @param sceneManageQueryReq 入参
     * @return 场景列表
     */
    ResponseResult<List<SceneManageListResp>> querySceneByStatus(SceneManageQueryReq sceneManageQueryReq);

    /**
     * 根据脚本发布id, 更新所有的场景对应该脚本的文件
     *
     * @param updateSceneFileRequest 请求入参
     * @return 响应参数
     */
    ResponseResult<Object> updateSceneFileByScriptId(CloudUpdateSceneFileRequest updateSceneFileRequest);

    /**
     * 保存
     *
     * @return -
     */
    ResponseResult<Long> saveScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 更新
     *
     * @return -
     */
    ResponseResult<String> updateScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 删除
     *
     * @return -
     */
    ResponseResult<String> deleteScene(SceneManageDeleteReq sceneManageDeleteReq);

    /**
     * 获取场景明细 供编辑使用
     *
     * @return -
     */
    ResponseResult<SceneManageWrapperResp> getSceneDetail(SceneManageIdReq sceneManageIdVO);

    /**
     * 获取压测场景列表
     *
     * @return -
     */
    ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq sceneManageQueryReq);

    /**
     * 流量计算
     *
     * @return -
     */
    ResponseResult<BigDecimal> calcFlow(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 获取机器数量范围
     *
     * @return -
     */
    ResponseResult<StrategyResp> getIpNum(SceneIpNumReq sceneIpNumReq);

    /**
     * 不分页查询所有场景
     *
     * @return -
     */
    ResponseResult<List<SceneManageListResp>> getSceneManageList(ContextExt traceContextExt);

    ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req);

    /**
     * 获取支持的jmeter插件列表
     *
     * @return -
     */
    ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> listEnginePlugins(
        EnginePluginFetchWrapperReq wrapperReq);

    /**
     * 获取支持的jmeter插件详情
     *
     * @return -
     */
    ResponseResult<EnginePluginDetailResp> getEnginePluginDetails(EnginePluginDetailsWrapperReq wrapperReq);

    /**
     * 校验并更新脚本
     *
     * @return -
     */
    ResponseResult<ScriptCheckResp> checkAndUpdateScript(ScriptCheckAndUpdateReq scriptCheckAndUpdateReq);

    /**
     * 解析脚本
     *
     * @param request 入参
     * @return 脚本节点信息
     */
    List<ScriptNode> scriptAnalyze(ScriptAnalyzeRequest request);
}
