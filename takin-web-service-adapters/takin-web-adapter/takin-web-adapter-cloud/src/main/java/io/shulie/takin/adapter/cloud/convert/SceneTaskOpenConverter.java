package io.shulie.takin.adapter.cloud.convert;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneStartTrialRunOutput;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskInspectStartReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface SceneTaskOpenConverter {

    SceneTaskOpenConverter INSTANCE = Mappers.getMapper(SceneTaskOpenConverter.class);

    /**
     * 脚本验证拓展
     *
     * @param scriptVerityRespExt 验证信息
     * @return 验证结果信息
     */
    ScriptCheckResp ofScriptVerityRespExt(ScriptVerityRespExt scriptVerityRespExt);

    /**
     * 启动日志返回结果转换
     *
     * @param sceneStartTrialRunOutput -
     * @return -
     */
    SceneActionResp ofSceneStartTrialRunOutput(SceneStartTrialRunOutput sceneStartTrialRunOutput);

    /**
     * 入参转换
     *
     * @param taskFlowDebugStartReq -
     * @return -
     */
    SceneManageWrapperInput ofTaskDebugDataStartReq(TaskFlowDebugStartReq taskFlowDebugStartReq);

    /**
     * 入参转换
     *
     * @param taskInspectStartReq -
     * @return -
     */
    SceneManageWrapperInput ofTaskInspectStartReq(TaskInspectStartReq taskInspectStartReq);

    /**
     * 入惨转换
     *
     * @param sceneTryRunTaskStartReq -
     * @return -
     */
    SceneManageWrapperInput ofSceneTryRunTaskReq(SceneTryRunTaskStartReq sceneTryRunTaskStartReq);

}
