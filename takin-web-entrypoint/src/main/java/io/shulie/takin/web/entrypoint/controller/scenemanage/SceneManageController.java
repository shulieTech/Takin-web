package io.shulie.takin.web.entrypoint.controller.scenemanage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerDeleteRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.ScenePositionPointResponse;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/4/17 ??????2:31
 */
@RestController
@RequestMapping("/api/scenemanage")
@Api(tags = "??????????????????")
public class SceneManageController {

    @Autowired
    private SceneManageService sceneManageService;

    @Autowired
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @PostMapping
    @ApiOperation("??????????????????")
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
    @ApiOperation("??????????????????")
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
        // cloud ??????????????????
        ResponseResult<SceneManageWrapperResp> oldData = sceneManageService.detailScene(sceneVO.getId());

        // ??????????????????...
        SceneManageWrapperDTO oldSceneData = JsonUtil.json2Bean(JsonUtil.bean2Json(oldData.getData()),
            SceneManageWrapperDTO.class);
        // ????????????
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
     * ?????????????????????
     *
     * @param sceneVO   ????????????????????????
     * @param sceneData ??????????????????
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
        //????????????????????????????????????????????????????????????????????????
        List<SceneBusinessActivityRefVO> oldBusinessActivityConfigList;
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefDTOList = sceneData.getBusinessActivityConfig();
        oldBusinessActivityConfigList = sceneBusinessActivityRefDTOList.stream().map(sceneBusinessActivityRefDTO -> {
            SceneBusinessActivityRefVO sceneBusinessActivityRefVO = new SceneBusinessActivityRefVO();
            sceneBusinessActivityRefVO.setBusinessActivityName(sceneBusinessActivityRefDTO.getBusinessActivityName());
            return sceneBusinessActivityRefVO;
        }).collect(Collectors.toList());
        //?????????????????????????????????
        if (!JSONObject.toJSONString(currentBusinessActivityConfigList).equals(
            JSONObject.toJSONString(oldBusinessActivityConfigList))) {
            List<SceneBusinessActivityRefVO> sceneBusinessActivityRefVOList = sceneVO.getBusinessActivityConfig();
            String businessActivityNames = sceneBusinessActivityRefVOList.stream().map(
                SceneBusinessActivityRefVO::getBusinessActivityName).collect(Collectors.joining(","));
            selectiveContent = selectiveContent + "????????????????????????" + businessActivityNames;
        }
        //????????????????????????
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
                selectiveContent = selectiveContent + "??????????????????" + fileNames;
            }
        }
        return selectiveContent;
    }

    @DeleteMapping
    @ApiOperation("??????????????????")
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
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "????????????????????????");
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
    @ApiOperation(value = "??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public SceneDetailResponse getDetail(@ApiParam(name = "id", value = "ID", required = true) Long id) {
        return sceneManageService.getById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<SceneManageListOutput>> getList(@ApiParam(name = "current", value = "??????", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "?????????", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "????????????ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "??????????????????") String sceneName,
        @ApiParam(name = "status", value = "????????????") Integer status,
        @ApiParam(name = "tagId", value = "??????id") Long tagId,
        @ApiParam(name = "lastPtStartTime", value = "??????????????????") String lastPtStartTime,
        @ApiParam(name = "lastPtEndTime", value = "??????????????????") String lastPtEndTime,
        @ApiParam(name = "recovery", value = "??????????????????") Boolean recovery
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
        ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
        return Response.success(responseResult.getData(), responseResult.getTotalNum());
    }

    /**
     * ????????????????????? ????????????
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
    @ApiOperation(value = "??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.DASHBOARD_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<SceneManageListOutput>> getListNoAuth(@ApiParam(name = "current", value = "??????", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "?????????", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "????????????ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "??????????????????") String sceneName,
        @ApiParam(name = "status", value = "????????????") Integer status,
        @ApiParam(name = "tagId", value = "??????id") Long tagId,
        @ApiParam(name = "lastPtStartTime", value = "??????????????????") String lastPtStartTime,
        @ApiParam(name = "lastPtEndTime", value = "??????????????????") String lastPtEndTime
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
    @ApiOperation(value = "????????????????????????")
    public ResponseResult<StrategyResp> getIpNum(
        @ApiParam(name = "concurrenceNum", value = "????????????")
        @RequestParam(value = "concurrenceNum", required = false) Integer concurrenceNum,
        @ApiParam(name = "tpsNum", value = "????????????")
        @RequestParam(value = "tpsNum", required = false) Integer tpsNum
    ) {
        return sceneManageService.getIpNum(concurrenceNum, tpsNum);
    }

    @DeleteMapping("/scheduler")
    @ApiOperation(value = "??????????????????")
    public void cancelSceneSchedulerPressure(@RequestBody SceneSchedulerDeleteRequest request) {
        sceneSchedulerTaskService.deleteBySceneId(request.getSceneId());

    }

    @GetMapping("/positionPoint")
    @ApiOperation(value = "??????????????????????????????????????????")
    public ResponseResult<List<ScenePositionPointResponse>> getPositionPoint(
        @ApiParam(name = "id", value = "????????????ID") Long id
    ) {
        return sceneManageService.getPositionPoint(id);
    }

    @PutMapping("/recovery")
    @ApiOperation(value = "??????????????????")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
            needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public WebResponse<String> recovery(@RequestBody @Valid SceneManageDeleteReq deleteVO) {
        return WebResponse.success(sceneManageService.recoveryScene(deleteVO));
    }

    @PutMapping("/archive")
    @ApiOperation("??????")
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
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "????????????????????????");
        }
        if (!SceneManageStatusEnum.ifFree(data.getStatus())) {
            OperationLogContextHolder.ignoreLog();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "????????????????????????????????????");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        SceneManageWrapperDTO sceneData = JSON.parseObject(JSON.toJSONString(data),
                SceneManageWrapperDTO.class);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneData.getPressureTestSceneName());
        String deleteSceneResponse = sceneManageService.archive(deleteVO);
        return WebResponse.success(deleteSceneResponse);
    }
}
