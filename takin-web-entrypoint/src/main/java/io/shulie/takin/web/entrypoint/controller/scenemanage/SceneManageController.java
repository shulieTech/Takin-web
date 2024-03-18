package io.shulie.takin.web.entrypoint.controller.scenemanage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDetailDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneScriptRefDTO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneScriptRefVO;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneBaseLineOutput;
import io.shulie.takin.web.biz.pojo.output.scene.TReportBaseLinkProblemOutput;
import io.shulie.takin.web.biz.pojo.request.scene.BaseLineQueryReq;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerDeleteRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneMachineResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.ScenePositionPointResponse;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/4/17 下午2:31
 */
@RestController
@RequestMapping("/api/scenemanage")
@Api(tags = "压测场景管理")
@Slf4j
public class SceneManageController {

    @Autowired
    private SceneManageService sceneManageService;

    @Autowired
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @PostMapping
    @ApiOperation("新增压测场景")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.CREATE
    )
    public WebResponse<Object> add(@RequestBody @Valid SceneManageWrapperVO sceneVO) throws TakinWebException {
        sceneManageService.checkParam(sceneVO);
        sceneManageService.addScene(sceneVO);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneVO.getPressureTestSceneName());
        return WebResponse.success();
    }

    @PutMapping
    @ApiOperation("修改压测场景")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public WebResponse<String> update(@RequestBody @Valid SceneManageWrapperVO sceneVO) {
        sceneManageService.checkParam(sceneVO);
        // cloud 获得场景详情
        ResponseResult<SceneManageWrapperResp> oldData = sceneManageService.detailScene(sceneVO.getId());

        // 数据转换一下...
        SceneManageWrapperDTO oldSceneData = JsonUtil.json2Bean(JsonUtil.bean2Json(oldData.getData()),
            SceneManageWrapperDTO.class);
        // 场景名称
        String sceneName = Optional.ofNullable(sceneVO.getPressureTestSceneName())
            .orElse(oldSceneData.getPressureTestSceneName());

        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(oldSceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneName);

        WebResponse<String> updateResponse = sceneManageService.updateScene(sceneVO);
        if (!updateResponse.getSuccess()) {
            return updateResponse;
        }
        String selectiveContent = this.compareChangeContent(sceneVO, oldSceneData);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_SELECTIVE_CONTENT, selectiveContent);
        if (!updateResponse.getSuccess()) {
            OperationLogContextHolder.ignoreLog();
        }
        return updateResponse;
    }

    /**
     * 比较更换的内容
     *
     * @param sceneVO   欲更新的场景内容
     * @param sceneData 本地场景内容
     * @return -
     */
    private String compareChangeContent(SceneManageWrapperVO sceneVO, SceneManageWrapperDTO sceneData) {
        String selectiveContent = "";
        List<SceneBusinessActivityRefVO> currentBusinessActivityConfigList = sceneVO.getBusinessActivityConfig();
        currentBusinessActivityConfigList = currentBusinessActivityConfigList.stream().map(
            sceneBusinessActivityRefVO -> {
                SceneBusinessActivityRefVO tmpSceneBusinessActivityRefVO = new SceneBusinessActivityRefVO();
                tmpSceneBusinessActivityRefVO.setBusinessActivityName(
                    sceneBusinessActivityRefVO.getBusinessActivityName());
                return tmpSceneBusinessActivityRefVO;
            }).collect(Collectors.toList());
        //转成同一业务活动对象再进行业务活动对象属性的比较
        List<SceneBusinessActivityRefVO> oldBusinessActivityConfigList;
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefDTOList = sceneData.getBusinessActivityConfig();
        oldBusinessActivityConfigList = sceneBusinessActivityRefDTOList.stream().map(sceneBusinessActivityRefDTO -> {
            SceneBusinessActivityRefVO sceneBusinessActivityRefVO = new SceneBusinessActivityRefVO();
            sceneBusinessActivityRefVO.setBusinessActivityName(sceneBusinessActivityRefDTO.getBusinessActivityName());
            return sceneBusinessActivityRefVO;
        }).collect(Collectors.toList());
        //比较业务活动的变更内容
        if (!JSONObject.toJSONString(currentBusinessActivityConfigList).equals(
            JSONObject.toJSONString(oldBusinessActivityConfigList))) {
            List<SceneBusinessActivityRefVO> sceneBusinessActivityRefVOList = sceneVO.getBusinessActivityConfig();
            String businessActivityNames = sceneBusinessActivityRefVOList.stream().map(
                SceneBusinessActivityRefVO::getBusinessActivityName).collect(Collectors.joining(","));
            selectiveContent = selectiveContent + "｜业务活动名称：" + businessActivityNames;
        }
        //比较脚本变更内容
        List<SceneScriptRefVO> currentSceneScriptRefVOList = sceneVO.getUploadFile();
        currentSceneScriptRefVOList = currentSceneScriptRefVOList
            .stream()
            .filter(sceneScriptRefVO -> 0 == sceneScriptRefVO.getIsDeleted())
            .map(sceneScriptRefVO -> {
                SceneScriptRefVO tmpSceneScriptRefVo = new SceneScriptRefVO();
                tmpSceneScriptRefVo.setFileName(sceneScriptRefVO.getFileName());
                tmpSceneScriptRefVo.setUploadTime(sceneScriptRefVO.getUploadTime());
                return tmpSceneScriptRefVo;
            }).collect(Collectors.toList());
        currentSceneScriptRefVOList.sort((o1, o2) -> {
            String o1FileName = o1.getFileName();
            String o2FileName = o2.getFileName();
            return o1FileName.compareTo(o2FileName);
        });

        List<SceneScriptRefVO> oldSceneScriptRefVOList;
        List<SceneScriptRefDTO> sceneScriptRefDTOList = sceneData.getUploadFile();
        oldSceneScriptRefVOList = sceneScriptRefDTOList.stream().map(sceneScriptRefDTO -> {
            SceneScriptRefVO sceneScriptRefVO = new SceneScriptRefVO();
            sceneScriptRefVO.setFileName(sceneScriptRefDTO.getFileName());
            sceneScriptRefVO.setUploadTime(sceneScriptRefDTO.getUploadTime());
            return sceneScriptRefVO;
        }).collect(Collectors.toList());
        oldSceneScriptRefVOList.sort((o1, o2) -> {
            String o1FileName = o1.getFileName();
            String o2FileName = o2.getFileName();
            return o1FileName.compareTo(o2FileName);
        });
        if (!JSONObject.toJSONString(currentSceneScriptRefVOList).equals(
            JSONObject.toJSONString(oldSceneScriptRefVOList))) {
            if (CollectionUtils.isNotEmpty(currentSceneScriptRefVOList)) {
                String fileNames = currentSceneScriptRefVOList.stream().map(SceneScriptRefVO::getFileName).collect(
                    Collectors.joining(","));
                selectiveContent = selectiveContent + "｜压测文件：" + fileNames;
            }
        }
        return selectiveContent;
    }

    @DeleteMapping
    @ApiOperation("删除压测场景")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.DELETE
    )
    public WebResponse<String> delete(@RequestBody @Valid SceneManageDeleteReq deleteVO) {
        ResponseResult<SceneManageWrapperResp> webResponse = sceneManageService.detailScene(deleteVO.getId());
        if (Objects.isNull(webResponse.getData())) {
            OperationLogContextHolder.ignoreLog();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "该压测场景不存在");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        SceneManageWrapperDTO sceneData = JSON.parseObject(JSON.toJSONString(webResponse.getData()),
            SceneManageWrapperDTO.class);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneData.getPressureTestSceneName());
        String deleteSceneResponse = sceneManageService.deleteScene(deleteVO);
        return WebResponse.success(deleteSceneResponse);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "压测场景详情")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public SceneDetailResponse getDetail(@ApiParam(name = "id", value = "ID", required = true) Long id) {
        return sceneManageService.getById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "压测场景列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<SceneManageListOutput>> getList(@ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "压测场景ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "压测场景名称") String sceneName,
        @ApiParam(name = "status", value = "压测状态") Integer status,
        @ApiParam(name = "tagId", value = "标签id") Long tagId,
        @ApiParam(name = "lastPtStartTime", value = "压测结束时间") String lastPtStartTime,
        @ApiParam(name = "lastPtEndTime", value = "压测结束时间") String lastPtEndTime,
        @ApiParam(name = "recovery", value = "是否是回收站") Boolean recovery,
        @ApiParam(name = "source", value = "来源") Integer source,
        @ApiParam(name = "configId", value = "单压测主键") Long configId
    ) {
        SceneManageQueryVO queryVO = new SceneManageQueryVO();

        queryVO.setPageNumber(current + 1);
        queryVO.setPageSize(pageSize);
        queryVO.setSceneId(sceneId);
        queryVO.setSceneName(sceneName);
        queryVO.setStatus(status);
        queryVO.setTagId(tagId);
        queryVO.setLastPtStartTime(lastPtStartTime);
        queryVO.setLastPtEndTime(lastPtEndTime);
        if(Objects.isNull(recovery)){
            recovery = false;
        }
        queryVO.setIsArchive(recovery?1:0);
        if(Objects.nonNull(source)){
            queryVO.setSource(source);
        }
        if(Objects.nonNull(configId)){
            queryVO.setConfigId(configId);
        }
        ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
        return Response.success(responseResult.getData(), responseResult.getTotalNum());
    }

    /**
     * 大盘无权限接口 场景接口
     * @param current
     * @param pageSize
     * @param sceneId
     * @param sceneName
     * @param status
     * @param tagId
     * @param lastPtStartTime
     * @param lastPtEndTime
     * @return
     */
    @GetMapping("/list/un_safe")
    @ApiOperation(value = "压测场景列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.DASHBOARD_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<SceneManageListOutput>> getListNoAuth(@ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "压测场景ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "压测场景名称") String sceneName,
        @ApiParam(name = "status", value = "压测状态") Integer status,
        @ApiParam(name = "tagId", value = "标签id") Long tagId,
        @ApiParam(name = "lastPtStartTime", value = "压测结束时间") String lastPtStartTime,
        @ApiParam(name = "lastPtEndTime", value = "压测结束时间") String lastPtEndTime
    ) {
        SceneManageQueryVO queryVO = new SceneManageQueryVO();

        queryVO.setPageNumber(current + 1);
        queryVO.setPageSize(pageSize);
        queryVO.setSceneId(sceneId);
        queryVO.setSceneName(sceneName);
        queryVO.setStatus(status);
        queryVO.setTagId(tagId);
        queryVO.setLastPtStartTime(lastPtStartTime);
        queryVO.setLastPtEndTime(lastPtEndTime);
        ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
        return Response.success(responseResult.getData(), responseResult.getTotalNum());
    }

    @GetMapping("/ipnum")
    @ApiOperation(value = "获取机器数量范围")
    public ResponseResult<StrategyResp> getIpNum(
        @ApiParam(name = "concurrenceNum", value = "并发数量")
        @RequestParam(value = "concurrenceNum", required = false) Integer concurrenceNum,
        @ApiParam(name = "tpsNum", value = "并发数量")
        @RequestParam(value = "tpsNum", required = false) Integer tpsNum
    ) {
        return sceneManageService.getIpNum(concurrenceNum, tpsNum);
    }

    @DeleteMapping("/scheduler")
    @ApiOperation(value = "取消定时启动")
    public void cancelSceneSchedulerPressure(@RequestBody SceneSchedulerDeleteRequest request) {
        sceneSchedulerTaskService.deleteBySceneId(request.getSceneId());

    }

    @GetMapping("/positionPoint")
    @ApiOperation(value = "获取待压测场景的脚本数据位点")
    public ResponseResult<List<ScenePositionPointResponse>> getPositionPoint(
        @ApiParam(name = "id", value = "压测场景ID") Long id
    ) {
        return sceneManageService.getPositionPoint(id);
    }

    @PutMapping("/recovery")
    @ApiOperation(value = "恢复压测场景")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
            needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public WebResponse<String> recovery(@RequestBody @Valid SceneManageDeleteReq deleteVO) {
        return WebResponse.success(sceneManageService.recoveryScene(deleteVO));
    }

    @PutMapping("/archive")
    @ApiOperation("归档")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
            subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
            logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_DELETE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
            needAuth = ActionTypeEnum.DELETE
    )
    public WebResponse<String> archive(@RequestBody @Valid SceneManageDeleteReq deleteVO) {
            ResponseResult<SceneManageWrapperResp> webResponse = sceneManageService.detailScene(deleteVO.getId());
        SceneManageWrapperResp data = webResponse.getData();
        if (Objects.isNull(data)) {
            OperationLogContextHolder.ignoreLog();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "该压测场景不存在");
        }
        if (!SceneManageStatusEnum.ifFree(data.getStatus())) {
            OperationLogContextHolder.ignoreLog();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "该压测场景状态不允许归档");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        SceneManageWrapperDTO sceneData = JSON.parseObject(JSON.toJSONString(data),
                SceneManageWrapperDTO.class);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneData.getPressureTestSceneName());
        String deleteSceneResponse = sceneManageService.archive(deleteVO);
        return WebResponse.success(deleteSceneResponse);
    }

    @ApiOperation(value = "压力机集群列表")
    @GetMapping("/machine")
    public WebResponse<SceneMachineResponse> machineClusters(@RequestParam String id, @RequestParam Integer type) {
        return WebResponse.success(sceneManageService.machineClusters(id, type));
    }

    @ApiOperation("设置性能基线")
    @PostMapping("/performanceLine/create")
    public ResponseResult<Boolean> performanceLineCrate(@RequestBody BaseLineQueryReq baseLineQueryReq){
        return ResponseResult.success(this.sceneManageService.performanceLineCreate(baseLineQueryReq));
    }

    @GetMapping("/getPerformanceLineResultList")
    @ApiOperation("获取性能基线数据")
    public ResponseResult<List<SceneBaseLineOutput>> getPerformanceLineResultList(@RequestParam("sceneId") long sceneId) {
        return ResponseResult.success(sceneManageService.getPerformanceLineResultList(sceneId));
    }

    @ApiOperation("根据场景id获取报告list")
    @GetMapping("/getReportListById")
    public ResponseResult<List<Long>> getReportListById(@RequestParam("id") Long id) {
        return ResponseResult.success(sceneManageService.getReportListById(id));
    }

    @ApiOperation("根据报告id获取流量明细快照")
    @GetMapping("/getTraceSnapShot")
    public ResponseResult<List<ReportTraceDetailDTO>> getTraceSnapShot(@RequestParam("reportId") long reportId){
        return ResponseResult.success(this.sceneManageService.getTraceSnapShot(reportId));
    }

    @ApiOperation("根据报告id获取报告问题列表")
    @GetMapping("/getReportProblemList")
    public ResponseResult<List<TReportBaseLinkProblemOutput>> getReportProblemList(@RequestParam("reportId") long reportId){
        log.info("getReportProblemList param,{}", reportId);
        return ResponseResult.success(this.sceneManageService.getReportProblemList(reportId));
    }


}
