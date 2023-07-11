package io.shulie.takin.web.diff.cloud.impl.scenetask;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneTryRunTaskStatusResp;

/**
 * @author qianshui
 * @date 2020/11/13 下午1:55
 */
@Service
public class SceneTaskApiImpl implements SceneTaskApi {

    @Resource(type = CloudTaskApi.class)
    private CloudTaskApi cloudTaskApi;

    @Resource(type = CloudReportApi.class)
    private CloudReportApi cloudReportApi;

    @Override
    public ResponseResult<String> stopTask(SceneManageIdReq req) {
        try {
            return ResponseResult.success(cloudTaskApi.stopTask(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<SceneActionResp> checkTask(SceneManageIdReq req) {
        try {
            return ResponseResult.success(cloudTaskApi.checkTask(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<String> addWarn(WarnCreateReq req) {
        try {
            return ResponseResult.success(cloudReportApi.addWarn(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<SceneJobStateResp> checkJobStateStatus(SceneManageIdReq req) {
        try {
            return ResponseResult.success(cloudTaskApi.checkSceneJobStatus(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<String> updateReportStatus(UpdateReportConclusionReq req) {
        try {
            return ResponseResult.success(cloudReportApi.updateReportConclusion(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(SceneTryRunTaskStartReq request) {
        try {
            return ResponseResult.success(cloudTaskApi.startTryRunTask(request));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<SceneTryRunTaskStatusResp> checkTryRunTaskStatus(SceneTryRunTaskCheckReq request) {
        try {
            return ResponseResult.success(cloudTaskApi.checkTaskStatus(request));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<SceneStartCheckResp> sceneStartPreCheck(SceneStartPreCheckReq checkReq) {
        try {
            return ResponseResult.success(cloudTaskApi.sceneStartPreCheck(checkReq));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<Boolean> callBackToWriteBalance(ScriptAssetBalanceReq req) {
        try {
            return ResponseResult.success(cloudTaskApi.callBackToWriteBalance(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<?> preStopTask(SceneManageIdReq req) {
        try {
            return ResponseResult.success(cloudTaskApi.boltStopTask(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

}
