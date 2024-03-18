package io.shulie.takin.web.biz.service.scenemanage;

import com.pamirs.takin.entity.domain.dto.report.ReportTraceDetailDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneBaseLineOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneListForSelectOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneReportListOutput;
import io.shulie.takin.web.biz.pojo.output.scene.TReportBaseLinkProblemOutput;
import io.shulie.takin.web.biz.pojo.request.scene.BaseLineQueryReq;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneForSelectRequest;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneReportRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneMachineResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.ScenePositionPointResponse;
import io.shulie.takin.web.common.domain.WebResponse;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/4/17 下午3:31
 */
public interface SceneManageService {

    /**
     * 添加场景
     *
     * @param vo 请求参数
     * @return 添加结果
     */
    WebResponse<List<SceneScriptRefOpen>> addScene(SceneManageWrapperVO vo);

    /**
     * 更新压测场景
     *
     * @param vo 更新所需的参数
     * @return 更新结果
     */
    WebResponse<String> updateScene(SceneManageWrapperVO vo);

    String deleteScene(SceneManageDeleteReq vo);

    ResponseResult<SceneManageWrapperResp> detailScene(Long id);

    ScriptCheckDTO checkBusinessActivityAndScript(SceneManageWrapperDTO sceneData);

    List<String> getAppIdsByBusinessActivityId(Long businessActivityId);

    ResponseResult<List<SceneManageListOutput>> getPageList(SceneManageQueryVO vo);

    ResponseResult<StrategyResp> getIpNum(Integer concurrenceNum, Integer tpsNum);

    ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req);

    void checkParam(SceneManageWrapperVO sceneVO);

    WebResponse<List<SceneScriptRefOpen>> buildSceneForFlowVerify(SceneManageWrapperVO vo, SceneManageWrapperReq req, Long userId);

    ResponseResult<List<ScenePositionPointResponse>> getPositionPoint(Long sceneId);

    /**
     * 根据场景id获得详情
     *
     * @param sceneId 场景id
     * @return 场景详情
     */
    SceneDetailResponse getById(Long sceneId);

    /**
     * 创建排除应用
     *
     * @param sceneId                场景id
     * @param excludedApplicationIds 排除应用ids
     */
    void createSceneExcludedApplication(Long sceneId, List<Long> excludedApplicationIds);

    /**
     * 从回收站恢复
     * @param
     * @return
     */
    String recoveryScene(SceneManageDeleteReq deleteVO);

	/**
     * 归档
     * @param vo
     * @return
     */
    String archive(SceneManageDeleteReq vo);

	/**
     * 下拉框的压测场景列表, 暂时只查询压测中状态的
     *
     * @param request 请求入参
     * @return 压测场景列表
     */
    List<SceneListForSelectOutput> listForSelect(ListSceneForSelectRequest request);

    /**
     * 通过场景id, 查询对应的正在运行的报告
     *
     * @param request 请求入参
     * @return 报告列表
     */
    List<SceneReportListOutput> listReportBySceneIds(ListSceneReportRequest request);

    /**
     * 报告排名
     *
     * @param request 请求入参
     * @return 报告排名
     */
    List<SceneReportListOutput> rankReport(ListSceneReportRequest request);

    SceneMachineResponse machineClusters(String id, Integer type);

    /**
     * 根据场景获取基线数据列表
     * @param sceneId
     * @return
     */
    List<SceneBaseLineOutput> getPerformanceLineResultList(long sceneId);

    /**
     * 根据时间段获取基线数据并入库
     * @return
     */
    boolean getBaseLineByTimeAndInsert(BaseLineQueryReq baseLineQueryReq);

    /**
     * 根据报告id获取基线数据并入库
     * @return
     */
    boolean getBaseLineByReportIdAndInsert(long reportId);

    boolean performanceLineCreate(BaseLineQueryReq baseLineQueryReq);

    /**
     * 获取报告的list，根据场景id
     * @param sceneId
     * @return
     */
    List<Long> getReportListById(Long sceneId);

    /**
     * 获取对比的问题列表
     * @param reportId
     * @return
     */
    List<TReportBaseLinkProblemOutput> getReportProblemList(long reportId);

    /**
     * 获取trace的快照
     * @param reportId
     * @return
     */
    List<ReportTraceDetailDTO> getTraceSnapShot(long reportId);

    boolean getBaseLineProblemAndInsert(long reportId);

    long countProblem(long reportId);

}
