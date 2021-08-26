package io.shulie.takin.web.biz.service.scenemanage;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.UpdateTpsRequest;
import io.shulie.takin.web.biz.pojo.response.scene.StartResponse;
import io.shulie.takin.web.common.domain.WebResponse;

/**
 * @author 莫问
 * @date 2020-04-22
 */
public interface SceneTaskService {

    WebResponse<StartResponse> startTask(SceneActionParam param);

    ResponseResult<String> stopTask(SceneActionParam param);

    /**
     * 校验
     *
     * @return
     */
    ResponseResult<SceneActionResp> checkStatus(Long sceneId, Long reportId);

    void updateTaskTps(UpdateTpsRequest request);

    Long queryTaskTps(Long reportId, Long sceneId);

    /**
     * 校验业务活动下的漏数配置
     * 每一级有错误, 就直接返回
     *
     * @param businessActivityIds 业务活动ids
     * @return 错误信息列表
     */
    List<String> checkBusinessActivitiesConfig(List<Long> businessActivityIds);

    String checkScriptCorrelation(SceneManageWrapperDTO sceneData);

    /**
     * 检查应用相关
     *
     * @param applicationMntList 应用列表
     * @return 错误信息
     */
    String checkApplicationCorrelation(List<TApplicationMnt> applicationMntList);

    /**
     * 通过场景ID从缓存获取报告ID
     * 在缓存过期时间范围内可以获取值
     *
     * @param sceneId 场景ID
     * @return
     */
    Long getReportIdFromCache(Long sceneId);

    /**
     * 通过压测场景ID获取压测任务状态：压测引擎
     *
     * @return
     */
    ResponseResult<SceneJobStateResp> checkSceneJobStatus(Long sceneId);
}
