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
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageIdVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneScriptRefVO;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
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
 * @date 2020/4/17 下午2:31
 */
@RestController
@RequestMapping("/api/scenemanage")
@Api(tags = "压测场景管理")
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
        SceneManageWrapperDTO oldSceneData = JsonUtil.json2bean(JsonUtil.bean2Json(oldData.getData()),
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
    public WebResponse delete(@RequestBody @Valid SceneManageIdVO deleteVO) {
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
        WebResponse deleteSceneResponse = sceneManageService.deleteScene(deleteVO);
        if (!deleteSceneResponse.getSuccess()) {
            OperationLogContextHolder.ignoreLog();
        }
        return deleteSceneResponse;
    }

    @GetMapping("/detail")
    @ApiOperation(value = "压测场景详情")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult getDetail(@ApiParam(name = "id", value = "ID", required = true) Long id) {
        return sceneManageService.detailScene(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "压测场景列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public WebResponse getList(@ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "压测场景ID") Long sceneId,
        @ApiParam(name = "sceneName", value = "压测场景名称") String sceneName,
        @ApiParam(name = "status", value = "压测状态") Integer status,
        @ApiParam(name = "tagId", value = "标签id") Long tagId,
        @ApiParam(name = "lastPtStartTime", value = "压测结束时间") String lastPtStartTime,
        @ApiParam(name = "lastPtEndTime", value = "压测结束时间") String lastPtEndTime
    ) {
        SceneManageQueryVO queryVO = new SceneManageQueryVO();
        queryVO.setCurrent(current);
        queryVO.setCurrentPage(current);
        queryVO.setPageSize(pageSize);
        queryVO.setSceneId(sceneId);
        queryVO.setSceneName(sceneName);
        queryVO.setStatus(status);
        queryVO.setTagId(tagId);
        queryVO.setLastPtStartTime(lastPtStartTime);
        queryVO.setLastPtEndTime(lastPtEndTime);
        return sceneManageService.getPageList(queryVO);
    }

    @GetMapping("/list/un_safe")
    @ApiOperation("接口: 压测场景列表, 无权限验证")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.DASHBOARD_SCENE,
        needAuth = ActionTypeEnum.QUERY
    )
    public WebResponse getListNoAuth(SceneManageQueryVO vo) {
        vo.setCurrentPage(vo.getCurrent());
        return sceneManageService.getPageList(vo);
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
}
