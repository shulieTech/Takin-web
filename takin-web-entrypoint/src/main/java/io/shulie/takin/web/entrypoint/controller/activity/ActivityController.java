package io.shulie.takin.web.entrypoint.controller.activity;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.annotation.ActivityCache;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityResultQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityVerifyResponse;
import io.shulie.takin.web.biz.pojo.response.activity.BusinessApplicationListResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ????????????
 *
 * @author shiyajian
 * create: 2020-12-29
 */
@RequestMapping("/api/activities")
@Api(tags = "??????????????????", value = "??????????????????")
@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @ApiOperation("|_ ??????????????????????????????")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "????????????ids", value = "businessActivityIds", required = true,
            dataType = "array", paramType = "query")
    })
    @GetMapping("/application/list")
    public List<BusinessApplicationListResponse> getActivityById(@RequestParam List<Long> businessActivityIds,
        String applicationName) {
        return activityService.listApplicationByBusinessActivityIds(businessActivityIds, applicationName);
    }

    @ApiOperation("??????????????????")
    @PostMapping("/create")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_ACTIVITY_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.CREATE
    )
    public void createActivity(@Validated @RequestBody ActivityCreateRequest request) {
        tryClearTemporaryActivity(request);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_ACTIVITY, request.getActivityName());
        OperationLogContextHolder.addVars(Vars.ENTRANCE_TYPE, request.getType().name());
        OperationLogContextHolder.addVars(Vars.APPLICATION_NAME, request.getApplicationName());
        OperationLogContextHolder.addVars(Vars.SERVICE_NAME, request.getServiceName());
        activityService.createActivity(request);
    }

    private void tryClearTemporaryActivity(ActivityCreateRequest request) {
        try {
            String temporaryKey = MD5Tool.getMD5(request.getApplicationName() + request.getLabel() + WebPluginUtils.traceTenantId() + WebPluginUtils.traceEnvCode());
            BusinessLinkManageTableEntity activity = activityService.getActivityByName(temporaryKey);
            if (null != activity) activityService.deleteActivity(activity.getLinkId());
        } catch (Exception e){
            //Ignore
        }
    }

    @PutMapping("/update")
    @ApiOperation("??????????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_ACTIVITY_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.UPDATE
    )
    public void updateActivity(@Validated @RequestBody ActivityUpdateRequest request) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_ACTIVITY, request.getActivityName());
        OperationLogContextHolder.addVars(Vars.ENTRANCE_TYPE, request.getType().name());
        OperationLogContextHolder.addVars(Vars.APPLICATION_NAME, request.getApplicationName());
        OperationLogContextHolder.addVars(Vars.SERVICE_NAME, request.getServiceName());
        activityService.updateActivity(request);
    }

    @ApiOperation("??????????????????")
    @DeleteMapping(value = "/delete")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_ACTIVITY_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.DELETE
    )
    public void deleteActivity(@Valid @NotNull @RequestParam Long activityId) {
        activityService.deleteActivity(activityId);
    }

    @ApiOperation("??????????????????")
    @GetMapping
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ActivityListResponse> pageActivities(@Valid ActivityQueryRequest request) {
        return activityService.pageActivities(request);
    }

    @ApiOperation("????????????????????????????????????")
    @GetMapping("/queryNormalActivities")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<ActivityListResponse> queryNormalActivities(@Valid ActivityResultQueryRequest request) {
        return activityService.queryNormalActivities(request);
    }

    @ApiOperation("|_ ??????????????????")
    @GetMapping("/activity")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    @ActivityCache
    public ActivityResponse getActivityById(@Valid ActivityInfoQueryRequest request) {
        return activityService.getActivityWithMetricsById(request);
    }

    @ApiOperation("|_ ??????????????????????????????")
    @GetMapping("/setActivityNodeState")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
            needAuth = ActionTypeEnum.UPDATE
    )
    public void setActivityNodeState(
            @RequestParam(value = "activityId") long activityId,
            @RequestParam(value = "ownerApps") @NotNull String ownerApps,
            @RequestParam(value = "serviceName") @NotNull String serviceName,
            @RequestParam(value = "state") boolean state) {

        activityService.setActivityNodeState(activityId, ownerApps, serviceName, state);
    }

    @ApiOperation("????????????????????????????????????")
    @PostMapping("/startVerify")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ActivityVerifyResponse verifyActivity(@Valid @RequestBody ActivityVerifyRequest request) {
        return activityService.verifyActivity(request);
    }

    @ApiOperation("????????????????????????????????????")
    @GetMapping("/verifyStat")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    public ActivityVerifyResponse getVerifyStatus(@Valid @RequestParam Long activityId) {
        return activityService.getVerifyStatus(activityId);
    }

    /*********************************************????????????????????????**************************************************/
    @ExceptionHandler(BindException.class)
    @ApiOperation("????????????????????????")
    @PostMapping("/virtual")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = BizOpConstants.Message.MESSAGE_VIRTUAL_BUSINESS_ACTIVITY_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.CREATE
    )
    public void createVirtualActivity(@Valid @RequestBody VirtualActivityCreateRequest request) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_ACTIVITY, request.getActivityName());
        OperationLogContextHolder.addVars(Vars.VIRTUAL_ENTRANCE, request.getVirtualEntrance());
        OperationLogContextHolder.addVars(Vars.ENTRANCE_TYPE, request.getType().getType());
        activityService.createVirtualActivity(request);
    }

    @PutMapping("/virtual")
    @ApiOperation("????????????????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_ACTIVITY,
        logMsgKey = BizOpConstants.Message.MESSAGE_VIRTUAL_BUSINESS_ACTIVITY_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.UPDATE
    )
    public void updateVirtualActivity(@Valid @RequestBody VirtualActivityUpdateRequest request) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_ACTIVITY, request.getActivityName());
        OperationLogContextHolder.addVars(Vars.ENTRANCE_TYPE, request.getType().getType());
        OperationLogContextHolder.addVars(Vars.VIRTUAL_ENTRANCE, request.getVirtualEntrance());
        activityService.updateVirtualActivity(request);
    }

    /*********************************************????????????????????????**************************************************/

}
