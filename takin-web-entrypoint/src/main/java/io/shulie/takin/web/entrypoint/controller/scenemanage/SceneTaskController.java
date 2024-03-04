package io.shulie.takin.web.entrypoint.controller.scenemanage;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.domain.vo.scenemanage.FileSplitResultVO;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.constant.VerifyTypeEnum;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneContactFileOutput;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStartRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStopRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.TaskPreStopRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.UpdateTpsRequest;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 莫问
 * @date 2020-04-27
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "scene/task/")
@Api(tags = "场景任务", value = "场景任务")
@Slf4j
public class SceneTaskController extends AbstractIndicators {
    @Autowired
    private SceneTaskApi sceneTaskApi;
    @Autowired
    private SceneTaskService sceneTaskService;
    @Autowired
    private VerifyTaskService verifyTaskService;
    @Autowired
    private SceneManageService sceneManageService;
    @Autowired
    private RedisClientUtil redisClientUtil;
    @Resource
    private FileSliceService fileSliceService;

    @ApiOperation("|_ 启动时停止")
    @PutMapping("/preStop")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_STOP
    )
    public void preStop(@Validated @RequestBody TaskPreStopRequest request) {
        sceneTaskService.preStop(request.getSceneId());
    }

    /**
     * 启动场景
     *
     * @param param 入参
     * @return 启动结果
     */
    @PostMapping("/start")
    @ApiOperation(value = "开始场景测试")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_START
    )
    public WebResponse<SceneActionResp> start(@RequestBody SceneActionParam param) {
        Long sceneId = param.getSceneId();
        try {
            // 标记压测启动
            redisClientUtil.hmset(PressureStartCache.getSceneResourceKey(sceneId), PressureStartCache.START_FLAG, "1");
            ResponseResult<SceneManageWrapperResp> webResponse = sceneManageService.detailScene(sceneId);
            OperationLogContextHolder.operationType(BizOpConstants.OpTypes.START);

            SceneManageWrapperDTO sceneData = BeanUtil.copyProperties(webResponse.getData(), SceneManageWrapperDTO.class);
            setStartLogVars(sceneData);

            //检测验证数据漏数任务
            List<String> errorMsgList = checkVerifyTaskConfig(param, sceneData);
            if (CollectionUtils.isNotEmpty(errorMsgList)) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, StringUtils.join(errorMsgList, Constants.SPLIT));
            }
            param.setResourceName(sceneData.getPressureTestSceneName());
            SceneActionResp startTaskResponse = sceneTaskService.startTask(param);
            // 开启漏数
            startCheckLeakTask(param, sceneData);
            sceneTaskService.checkStatus(sceneData.getId(), startTaskResponse.getData());
            return WebResponse.success(startTaskResponse);
        } catch (TakinWebException ex) {
            // 解除 场景锁
            notifyStartFail(sceneId, ex.getMessage());
            redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(sceneId));
            SceneActionResp sceneStart = new SceneActionResp();
            //sceneStart.setMsg(Arrays.asList(StringUtils.split(ex.getMessage(), Constants.SPLIT)));
            List<String> message = Lists.newArrayList();
            if(StringUtils.isNotBlank(ex.getMessage())) {
                message.addAll(Collections.singletonList(ex.getMessage()));
            }
            if(ex.getSource() != null) {
                message.addAll(Collections.singletonList(JsonHelper.bean2Json(ex.getSource())));
            }
            sceneStart.setMsg(message.stream().distinct().collect(Collectors.toList()));
            return WebResponse.success(sceneStart);
        }
    }

    private void startCheckLeakTask(SceneActionParam param, SceneManageWrapperDTO sceneData) {
        if (param.getLeakSqlEnable() != null && param.getLeakSqlEnable()) {
            //添加开始漏数的查询效果
            LeakVerifyTaskStartRequest startRequest = new LeakVerifyTaskStartRequest();
            startRequest.setRefType(VerifyTypeEnum.SCENE.getCode());
            startRequest.setRefId(param.getSceneId());
            //查询报告id
            SceneManageIdReq req = new SceneManageIdReq();
            req.setId(param.getSceneId());
            WebPluginUtils.fillCloudUserData(req);
            ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
            SceneActionResp resp = JsonHelper.json2Bean(JsonHelper.bean2Json(response.getData()),
                SceneActionResp.class);
            startRequest.setReportId(resp.getReportId());
            startRequest.setTimeInterval(sceneData.getScheduleInterval());
            List<Long> businessActivityIds =
                sceneData.getBusinessActivityConfig().stream().map(
                    SceneBusinessActivityRefDTO::getBusinessActivityId).collect(
                    Collectors.toList());
            startRequest.setBusinessActivityIds(businessActivityIds);
            verifyTaskService.start(startRequest);
        }
    }

    private void setStartLogVars(SceneManageWrapperDTO sceneData) {
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneData.getPressureTestSceneName());
    }

    /**
     * 停止压测
     *
     * @param param 入参
     * @return 停止结果
     */
    @PostMapping("/stop")
    @ApiOperation(value = "结束场景测试")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_STOP
    )
    public ResponseResult<String> stop(@RequestBody SceneActionParam param) {
        ResponseResult<SceneManageWrapperResp> webResponse = sceneManageService.detailScene(param.getSceneId());
        if (Objects.isNull(webResponse.getData())) {
            OperationLogContextHolder.ignoreLog();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "该压测场景不存在");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.STOP);
        SceneManageWrapperDTO sceneData = JSON.parseObject(JSON.toJSONString(webResponse.getData()),
            SceneManageWrapperDTO.class);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneData.getPressureTestSceneName());
        ResponseResult<String> stopResult = sceneTaskService.stopTask(param);
        if (stopResult == null || !stopResult.getSuccess()) {
            OperationLogContextHolder.ignoreLog();
        }
        //如果有验证任务，则同时停止验证任务
        LeakVerifyTaskStopRequest stopRequest = new LeakVerifyTaskStopRequest();
        stopRequest.setRefType(VerifyTypeEnum.SCENE.getCode());
        stopRequest.setRefId(param.getSceneId());
        verifyTaskService.stop(stopRequest);
        return stopResult;
    }

    /**
     * 检查压测场景启动状态
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @return 启动状态
     */
    @GetMapping("/checkStartStatus")
    @ApiOperation(value = "检查启动状态")
    public ResponseResult<SceneActionResp> checkStartStatus(@RequestParam("sceneId") Long sceneId, @RequestParam("reportId") Long reportId) {
        // 前端校验逻辑，data = 0,错误信息才会显示。也就是有这样的情况：0 -> 1 -> 0 (检测到压测引擎错误信息)
        return sceneTaskService.checkStatus(sceneId, reportId);
    }

    @PutMapping("/tps")
    @ApiOperation(value = "修改运行中")
    public void updateTaskTps(@Validated @RequestBody UpdateTpsRequest request) {
        sceneTaskService.updateTaskTps(request);
    }

    @GetMapping("/queryTaskTps")
    @ApiOperation(value = "查询运行中修改之后的tps")
    public ResponseResult<Long> queryTaskTps(@RequestParam Long reportId, @RequestParam Long sceneId, @RequestParam String xpathMd5) {
        Long totalTps = sceneTaskService.queryTaskTps(reportId, sceneId, xpathMd5);
        return ResponseResult.success(totalTps);
    }

    private List<String> checkVerifyTaskConfig(SceneActionParam param, SceneManageWrapperDTO sceneData) {
        //校验漏数验证时间间隔是否配置
        if ((param.getLeakSqlEnable() != null && param.getLeakSqlEnable())) {
            if (Objects.isNull(sceneData.getScheduleInterval())) {
                return Collections.singletonList("请先配置漏数验证时间间隔");
            }

            //校验压测场景下是否存在漏数配置
            List<Long> businessActivityIds = sceneData.getBusinessActivityConfig().stream()
                .map(SceneBusinessActivityRefDTO::getBusinessActivityId)
                .collect(Collectors.toList());
            return sceneTaskService.checkBusinessActivitiesConfig(businessActivityIds);
        }

        return null;
    }

    @PostMapping("preCheck")
    @ApiOperation("前置校验")
    public ResponseResult<Object> preCheck(@RequestBody SceneActionParam param) {
        if (Objects.isNull(param.getMachineId())) {
            return ResponseResult.fail("未选择压力机集群", null);
        }

        return ResponseResult.success(sceneTaskService.preCheck(param));
    }

    @PostMapping("script/contactScene")
    @ApiModelProperty(value = "大文件关联场景")
    public ResponseResult<?> preSplitFile(@RequestBody FileSplitResultVO resultVO) {
        try {
            SceneContactFileOutput output = fileSliceService.contactScene(new SceneBigFileSliceParam() {{
                setFileName(resultVO.getFileName());
                setSceneId(resultVO.getSceneId());
                setIsSplit(resultVO.getIsSplit());
                setIsOrderSplit(resultVO.getIsOrderSplit());
            }});
            return ResponseResult.success(output);
        } catch (TakinCloudException e) {
            return ResponseResult.fail("关联文件与脚本、场景异常", e.getMessage());
        }
    }

    @PutMapping("forceStop")
    public ResponseResult<String> forceStop(String resourceId) {
        try {
            sceneTaskService.forceStop(resourceId);
            return ResponseResult.success("强制停止成功");
        } catch (Exception e) {
            return ResponseResult.fail(e.getMessage(), null);
        }
    }

    private void notifyStartFail(Long sceneId, String message) {
        Object resource = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
            PressureStartCache.RESOURCE_ID);
        if (Objects.nonNull(resource)) {
            callStartFailedEvent(String.valueOf(resource), message);
        }
    }
}
