package io.shulie.takin.adapter.cloud.impl.scene.task;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.adapter.api.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.adapter.api.model.common.SlaBean;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.adapter.api.model.request.scenetask.*;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneTaskStopResp;
import io.shulie.takin.adapter.cloud.convert.SceneTaskOpenConverter;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.scenemanage.*;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scenetask.*;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.ext.content.asset.AssetBalanceExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/11/13 上午11:06
 */
@Service
@Slf4j
public class CloudTaskApiImpl implements CloudTaskApi {

    @Resource
    private CloudSceneTaskService sceneTaskService;

    @Resource
    private CloudReportService reportService;


    @Override
    public SceneActionResp start(SceneTaskStartReq req) {
        CloudPluginUtils.fillUserData(req);
        SceneTaskStartInput input = BeanUtil.copyProperties(req, SceneTaskStartInput.class);
        // 设置用户主键
        input.setOperateId(req.getUserId());
        // 启动场景
        SceneActionOutput output = sceneTaskService.start(input);
        return BeanUtil.copyProperties(output, SceneActionResp.class);
    }

    @Override
    public String stopTask(SceneManageIdReq req) {
        //记录下sla的数据
        if (req.getReportId() != null) {
            UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
            slaDataInput.setReportId(req.getReportId());
            slaDataInput.setSlaBean(BeanUtil.copyProperties(req.getSlaBean(), SlaBean.class));
            reportService.updateReportSlaData(slaDataInput);
        }
        log.info("任务{}-{} ，原因：web 调 cloud 触发停止", req.getId(), req.getReportId());
        // 与sla操作是一致的
        sceneTaskService.stop(req.getId());
        return "停止场景成功";

    }

    @Override
    public Integer boltStopTask(SceneManageIdReq req) {
        return sceneTaskService.blotStop(req);
    }

    @Override
    public SceneActionResp checkTask(SceneManageIdReq req) {
        SceneActionOutput sceneAction = sceneTaskService.checkSceneTaskStatus(req.getId(), req.getReportId());
        SceneActionResp resp = new SceneActionResp();
        resp.setData(sceneAction.getData());
        resp.setMsg(sceneAction.getMsg());
        resp.setReportId(sceneAction.getReportId());
        return resp;
    }

    @Override
    public String updateSceneTaskTps(SceneTaskUpdateTpsReq req) {
        SceneTaskUpdateTpsInput input = new SceneTaskUpdateTpsInput();
        input.setSceneId(req.getSceneId());
        input.setReportId(req.getReportId());
        input.setTpsNum(req.getTpsNum());
        input.setXpathMd5(req.getXpathMd5());
        sceneTaskService.updateSceneTaskTps(input);
        return "tps更新成功";
    }

    @Override
    public SceneTaskAdjustTpsResp queryAdjustTaskTps(SceneTaskQueryTpsReq req) {
        SceneTaskQueryTpsInput input = new SceneTaskQueryTpsInput();
        input.setSceneId(req.getSceneId());
        input.setReportId(req.getReportId());
        input.setXpathMd5(req.getXpathMd5());
        double value = sceneTaskService.queryAdjustTaskTps(input);
        SceneTaskAdjustTpsResp sceneTaskAdjustTpsResp = new SceneTaskAdjustTpsResp();
        sceneTaskAdjustTpsResp.setTotalTps(Double.valueOf(value).longValue());
        return sceneTaskAdjustTpsResp;
    }

    @Override
    public Long startFlowDebugTask(TaskFlowDebugStartReq req) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofTaskDebugDataStartReq(req);
        // 设置用户
        input.setOperateId(input.getUserId());
        input.setOperateName(input.getUserName());
        //压测引擎插件需要传入插件id和插件版本 modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(req.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = req.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput() {{
                setPluginId(plugin.getPluginId());
                setPluginVersion(plugin.getVersion());
            }}).collect(Collectors.toList());
        }
        return sceneTaskService.startFlowDebugTask(input, enginePluginInputs);
    }

    @Override
    public SceneInspectTaskStartResp startInspectTask(TaskInspectStartReq req) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofTaskInspectStartReq(req);

        //压测引擎插件需要传入插件id和插件版本 modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(req.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = req.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput() {{
                setPluginId(plugin.getPluginId());
                setPluginVersion(plugin.getVersion());
            }}).collect(Collectors.toList());
        }
        SceneInspectTaskStartOutput output = sceneTaskService.startInspectTask(input, enginePluginInputs);
        return BeanUtil.copyProperties(output, SceneInspectTaskStartResp.class);
    }

    @Override
    public SceneInspectTaskStopResp stopInspectTask(TaskInspectStopReq req) {
        SceneInspectTaskStopOutput output = sceneTaskService.stopInspectTask(req.getSceneId());
        return BeanUtil.copyProperties(output, SceneInspectTaskStopResp.class);
    }

    @Override
    public SceneTaskStopResp forceStopInspectTask(TaskStopReq req) {
        SceneTaskStopOutput output = sceneTaskService.forceStopTask(req.getReportId(), req.isFinishReport());
        return BeanUtil.copyProperties(output, SceneTaskStopResp.class);
    }

    @Override
    public SceneTryRunTaskStartResp startTryRunTask(SceneTryRunTaskStartReq req) {
        SceneManageWrapperInput input = SceneTaskOpenConverter.INSTANCE.ofSceneTryRunTaskReq(req);
        // 设置用户
        CloudPluginUtils.fillUserData(input);
        input.setOperateId(input.getUserId());
        input.setOperateName(input.getUserName());
        //压测引擎插件需要传入插件id和插件版本 modified by xr.l 20210712
        List<EnginePluginInput> enginePluginInputs = null;
        if (CollectionUtils.isNotEmpty(req.getEnginePlugins())) {
            List<EnginePluginsRefOpen> enginePlugins = req.getEnginePlugins();
            enginePluginInputs = enginePlugins.stream().map(plugin -> new EnginePluginInput() {{
                setPluginId(plugin.getPluginId());
                setPluginVersion(plugin.getVersion());
            }}).collect(Collectors.toList());
        }
        SceneTryRunTaskStartOutput output = sceneTaskService.startTryRun(input, enginePluginInputs);
        return BeanUtil.copyProperties(output, SceneTryRunTaskStartResp.class);
    }

    @Override
    public SceneTryRunTaskStatusResp checkTaskStatus(SceneTryRunTaskCheckReq req) {
        SceneTryRunTaskStatusOutput output = sceneTaskService.checkTaskStatus(req.getSceneId(), req.getReportId());
        return BeanUtil.copyProperties(output, SceneTryRunTaskStatusResp.class);
    }

    @Override
    public SceneJobStateResp checkSceneJobStatus(SceneManageIdReq req) {
        SceneJobStateOutput jobState = sceneTaskService.checkSceneJobStatus(req.getId());
        SceneJobStateResp resp = new SceneJobStateResp();
        resp.setState(jobState.getState());
        resp.setMsg(jobState.getMsg());
        return resp;
    }

    @Override
    public SceneStartCheckResp sceneStartPreCheck(SceneStartPreCheckReq req) {
        SceneTaskStartCheckInput input = new SceneTaskStartCheckInput();
        input.setSceneId(req.getSceneId());
        SceneTaskStartCheckOutput output = sceneTaskService.sceneStartCsvPositionCheck(input);
        return BeanUtil.copyProperties(output, SceneStartCheckResp.class);
    }

    @Override
    public Boolean callBackToWriteBalance(ScriptAssetBalanceReq req) {
        AssetBalanceExt ext = BeanUtil.copyProperties(req, AssetBalanceExt.class);
        sceneTaskService.writeBalance(ext);
        return true;
    }
}
