package io.shulie.takin.web.diff.api.scenemanage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import io.shulie.takin.cloud.open.req.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.open.req.scenemanage.*;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import org.springframework.validation.annotation.Validated;

/**
 * @author 无涯
 * @date 2020/10/27 4:22 下午
 */
@Valid
public interface SceneManageApi {

    /**
     * 根据脚本发布id, 更新所有的场景对应该脚本的文件
     *
     * @param updateSceneFileRequest 请求入参
     * @return 响应参数
     */
    ResponseResult<Object> updateSceneFileByScriptId(@Validated CloudUpdateSceneFileRequest updateSceneFileRequest);

    /**
     * 保存
     *
     * @return
     */
    ResponseResult<Long> saveScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 更新
     *
     * @return
     */
    ResponseResult<String> updateScene(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 删除
     *
     * @return
     */
    ResponseResult deleteScene(SceneManageDeleteReq sceneManageDeleteReq);

    /**
     * 获取场景明细 供编辑使用
     *
     * @return
     */
    ResponseResult<SceneManageWrapperResp> getSceneDetail(SceneManageIdReq sceneManageIdVO);

    /**
     * 获取压测场景列表
     *
     * @return
     */
    ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq sceneManageQueryReq);

    /**
     * 流量计算
     *
     * @return
     */
    ResponseResult<BigDecimal> calcFlow(SceneManageWrapperReq sceneManageWrapperReq);

    /**
     * 获取机器数量范围
     *
     * @return
     */
    ResponseResult<StrategyResp> getIpNum(SceneIpNumReq sceneIpNumReq);

    /**
     * 不分页查询所有场景
     *
     * @return
     */
    ResponseResult<List<SceneManageListResp>> getSceneManageList(CloudUserCommonRequestExt requestExt);

    ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req);

    /**
     * 获取支持的jmeter插件列表
     *
     * @return
     */
    ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> listEnginePlugins(
        EnginePluginFetchWrapperReq wrapperReq);

    /**
     * 获取支持的jmeter插件详情
     *
     * @return
     */
    ResponseResult<EnginePluginDetailResp> getEnginePluginDetails(EnginePluginDetailsWrapperReq wrapperReq);

    /**
     * 校验并更新脚本
     *
     * @return
     */
    ResponseResult<ScriptCheckResp> checkAndUpdateScript(ScriptCheckAndUpdateReq scriptCheckAndUpdateReq);

    /**
     * 解析脚本获取到脚本节点信息
     * @param request
     * @return
     */
    ResponseResult<List<ScriptNode>> scriptAnalyze(ScriptAnalyzeRequest request);
}
