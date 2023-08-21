package io.shulie.takin.cloud.biz.service.scene;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneActionOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneJobStateOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStartOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStatusOutput;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.ext.content.asset.AssetBalanceExt;

/**
 * @author 莫问
 * @date 2020-04-22
 */
public interface CloudSceneTaskService {

    /**
     * 启动场景测试
     *
     * @param input 入参
     * @return -
     */
    SceneActionOutput start(SceneTaskStartInput input);

    /**
     * 停止场景测试
     *
     * @param sceneId 场景主键
     */
    void stop(Long sceneId);

    /**
     * 停止场景测试
     * <p>直接模式-手工补偿</p>
     * <ui>
     * <li>重置场景状态为0</li>
     * <li>重置对应的最新的压测报告状态为2</li>
     * </ui>
     *
     * @param req 取消参数
     * @return 业务码, 1 成功, 2 调用停止压测
     */
    int blotStop(SceneManageIdReq req);

    /**
     * 检查场景压测启动状态
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @return -
     */
    SceneActionOutput checkSceneTaskStatus(Long sceneId, Long reportId);

    /**
     * 处理场景任务事件
     *
     * @param taskResult 任务执行结果
     */
    void handleSceneTaskEvent(TaskResult taskResult);

    /**
     * 结束标识，之后并不是pod生命周期结束，而是metric数据传输完毕，将状态回置成压测停止
     *
     * @param param 入参
     * @return -
     */
    String taskResultNotify(SceneTaskNotifyParam param);

    /**
     * 开始任务试跑
     *
     * @param input         入参
     * @param enginePlugins 压测引擎列表
     * @return -
     */
    SceneTryRunTaskStartOutput startTryRun(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins,
                                           SceneTryRunTaskStartReq debugCloudRequest);

    /**
     * 调整压测任务的tps
     *
     * @param input 入参
     */
    void updateSceneTaskTps(SceneTaskUpdateTpsInput input);

    /**
     * 查询当前调整压测任务的tps
     *
     * @param input 入参
     * @return -
     */
    double queryAdjustTaskTps(SceneTaskQueryTpsInput input);

    /**
     * 启动流量调试，返回报告id
     *
     * @param input         入参
     * @param enginePlugins 压测引擎列表
     * @return -
     */
    Long startFlowDebugTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins);

    /**
     * 启动巡检场景
     *
     * @param input         入参
     * @param enginePlugins 压测引擎列表
     * @return -
     */
    SceneInspectTaskStartOutput startInspectTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins);

    /**
     * 停止巡检场景
     *
     * @param sceneId 场景主键
     * @return -
     */
    SceneInspectTaskStopOutput stopInspectTask(Long sceneId);

    /**
     * 强制停止任务，不考虑数据的安全性，数据会丢失
     *
     * @param isNeedFinishReport 是否需要强制停止
     * @param reportId           报告主键
     * @return 场景停止结果
     */
    SceneTaskStopOutput forceStopTask(Long reportId, boolean isNeedFinishReport);

    /**
     * 查询流量试跑状态
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @return -
     */
    SceneTryRunTaskStatusOutput checkTaskStatus(Long sceneId, Long reportId);

    /**
     * 检查巡检任务状态：压测引擎
     *
     * @param sceneId 场景主键
     * @return -
     */
    SceneJobStateOutput checkSceneJobStatus(Long sceneId);

    /**
     * 开始压测前检查文件位点
     *
     * @param input -
     * @return -
     */
    SceneTaskStartCheckOutput sceneStartCsvPositionCheck(SceneTaskStartCheckInput input);

    /**
     * 清除位点缓存
     *
     * @param sceneId 场景主键
     */
    void cleanCachedPosition(Long sceneId);

    /**
     * 回写流量账户
     *
     * @param balanceExt 账户余额数据
     */
    void writeBalance(AssetBalanceExt balanceExt);

    PressureTaskEntity initPressureTask(SceneManageWrapperOutput scene, SceneTaskStartInput input);

    ReportEntity initReport(SceneManageWrapperOutput scene, SceneTaskStartInput input, PressureTaskEntity pressureTask);
}
