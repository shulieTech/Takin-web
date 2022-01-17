package io.shulie.takin.web.diff.cloud.impl.scenetask;

import java.util.List;

import io.shulie.takin.cloud.open.api.report.CloudReportApi;
import io.shulie.takin.cloud.open.api.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdsReq;
import io.shulie.takin.cloud.open.req.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.open.req.report.WarnCreateReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.open.resp.report.ReportActivityResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author qianshui
 * @date 2020/11/13 下午1:55
 */
@Service
public class SceneTaskApiImpl implements SceneTaskApi {

    @Autowired
    private CloudTaskApi cloudTaskApi;

    @Autowired
    private CloudReportApi cloudReportApi;

    @Override
    public ResponseResult<String> stopTask(SceneManageIdReq req) {
        return cloudTaskApi.stopTask(req);
    }

    @Override
    public ResponseResult<SceneActionResp> checkTask(SceneManageIdReq req) {
        return cloudTaskApi.checkTask(req);
    }

    @Override
    public ResponseResult<String> addWarn(WarnCreateReq req) {
        return cloudReportApi.addWarn(req);
    }

    @Override
    public ResponseResult<SceneJobStateResp> checkJobStateStatus(SceneManageIdReq req) {
        return cloudTaskApi.checkSceneJobSstatus(req);
    }

    @Override
    public ResponseResult<String> updateReportStatus(UpdateReportConclusionReq req) {
        return cloudReportApi.updateReportConclusion(req);
    }

    @Override
    public ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(SceneTryRunTaskStartReq request) {
        return cloudTaskApi.startTryRunTask(request);
    }

    @Override
    public ResponseResult<SceneTryRunTaskStatusResp> checkTryRunTaskStatus(SceneTryRunTaskCheckReq request) {
        return cloudTaskApi.checkTaskStatus(request);
    }

    @Override
    public ResponseResult<SceneStartCheckResp> sceneStartPreCheck(SceneStartPreCheckReq checkReq) {
        return cloudTaskApi.sceneStartPreCheck(checkReq);
    }

    @Override
    public ResponseResult<Boolean> callBackToWriteBalance(ScriptAssetBalanceReq req) {
        return cloudTaskApi.callBackToWriteBalance(req);
    }

    @Override
    public ResponseResult<List<ReportActivityResp>> listQueryTpsParam(ReportDetailByIdsReq reportDetailByIdsReq) {
        return cloudReportApi.getActivities(reportDetailByIdsReq);
    }

}
