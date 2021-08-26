package io.shulie.takin.web.diff.api.scenetask;

import io.shulie.takin.cloud.open.req.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.open.req.report.WarnCreateReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author qianshui
 * @date 2020/11/13 下午1:54
 */
public interface SceneTaskApi {

    ResponseResult<String> stopTask(SceneManageIdReq req);

    ResponseResult<SceneActionResp> checkTask(SceneManageIdReq req);

    ResponseResult<String> updateReportStatus(UpdateReportConclusionReq req);

    /**
     * 启动脚本调试
     *
     * @param request 启动参数
     * @return 响应相关数据
     */
    ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(SceneTryRunTaskStartReq request);

    /**
     * 查询脚本调试状态
     *
     * @param request 启动参数
     * @return 响应相关数据
     */
    ResponseResult<SceneTryRunTaskStatusResp> checkTryRunTaskStatus(SceneTryRunTaskCheckReq request);

    /**
     * 创建告警
     *
     * @param req 请求参数
     * @return 操作结果
     */
    ResponseResult<String> addWarn(WarnCreateReq req);

    /**
     * 获取压测任务状态：压测引擎
     *
     * @return -
     */
    ResponseResult<SceneJobStateResp> checkJobStateStatus(SceneManageIdReq req);

    ResponseResult<SceneStartCheckResp> sceneStartPreCheck(SceneStartPreCheckReq checkReq);
}
