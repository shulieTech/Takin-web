package io.shulie.takin.adapter.api.entrypoint.scenetask;

import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskInspectStartReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskInspectStopReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskStopReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneTaskStopResp;

/**
 * 压测任务
 *
 * @author qianshui
 * @date 2020/11/13 上午11:05
 */
public interface CloudTaskApi {

    /**
     * 启动压测
     *
     * @param req -
     * @return -
     */
    SceneActionResp start(SceneTaskStartReq req);

    /**
     * 停止任务
     *
     * @param req 入参
     * @return 停止结果
     */
    String stopTask(SceneManageIdReq req);

    /**
     * 强制停止任务
     *
     * @param req 请求入参
     * @return 停止结果
     */
    Integer boltStopTask(SceneManageIdReq req);

    /**
     * 检查任务状态
     *
     * @param req 入参
     * @return 状态检查返回值
     */
    SceneActionResp checkTask(SceneManageIdReq req);

    /**
     * 更新压测场景任务tps
     *
     * @param sceneTaskUpdateTpsReq -
     * @return -
     */
    String updateSceneTaskTps(SceneTaskUpdateTpsReq sceneTaskUpdateTpsReq);

    /**
     * 获取调整前tps
     *
     * @param sceneTaskQueryTpsReq -
     * @return -
     */
    SceneTaskAdjustTpsResp queryAdjustTaskTps(SceneTaskQueryTpsReq sceneTaskQueryTpsReq);

    /**
     * 启动流量调试任务
     *
     * @param taskFlowDebugStartReq -
     * @return -
     */
    Long startFlowDebugTask(TaskFlowDebugStartReq taskFlowDebugStartReq);

    /**
     * 启动巡检任务
     *
     * @param taskInspectStartReq -
     * @return -
     */
    SceneInspectTaskStartResp startInspectTask(TaskInspectStartReq taskInspectStartReq);

    /**
     * 停止巡检任务
     *
     * @param taskInspectStopReq -
     * @return -
     */
    SceneInspectTaskStopResp stopInspectTask(TaskInspectStopReq taskInspectStopReq);

    /**
     * 强制停止任务
     * <p>可能会造成压测数据丢失</p>
     *
     * @param taskStopReq -
     * @return -
     */
    SceneTaskStopResp forceStopInspectTask(TaskStopReq taskStopReq);

    /**
     * 启动试跑任务
     *
     * @param sceneTryRunTaskStartReq -
     * @return -
     */
    SceneTryRunTaskStartResp startTryRunTask(SceneTryRunTaskStartReq sceneTryRunTaskStartReq);

    /**
     * 查询试跑任务状态
     *
     * @param sceneTryRunTaskCheckReq -
     * @return -
     */
    SceneTryRunTaskStatusResp checkTaskStatus(SceneTryRunTaskCheckReq sceneTryRunTaskCheckReq);

    /**
     * 检查压测场景任务状态
     *
     * @param req -
     * @return -
     */
    SceneJobStateResp checkSceneJobStatus(SceneManageIdReq req);

    /**
     * 压测场景启动前检查
     *
     * @param req -
     * @return -
     */
    SceneStartCheckResp sceneStartPreCheck(SceneStartPreCheckReq req);

    /**
     * 回调写入余额
     *
     * @param req 入参
     * @return 操作结果
     */
    Boolean callBackToWriteBalance(ScriptAssetBalanceReq req);

}
