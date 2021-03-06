package io.shulie.takin.web.entrypoint.controller.confcenter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.github.pagehelper.util.StringUtil;

import com.pamirs.takin.entity.domain.vo.AppOperateAgentCheckVO;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.service.ApplicationService;
import com.pamirs.takin.entity.domain.vo.AppUninstallAgentVO;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationQueryRequestV2;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationListResponseV2;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntrancesResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationVisualInfoQueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @author liuchuan
 * @date 2020-03-16 14:55
 */

@RestController("confApplicationController")
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "??????: ??????????????????", value = "??????????????????")
public class ApplicationController {

    private static final String FALSE_CODE = "0";

    @Resource
    private ActivityService activityService;
    @Resource
    private ApplicationService applicationService;
    @Resource
    private ApplicationEntranceClient applicationEntranceClient;

    @GetMapping("/application/center/list")
    @ApiOperation("|_ ????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ApplicationListResponseV2> pageApplicationWithAuth(ApplicationQueryRequestV2 request) {
        return applicationService.pageApplication(request);
    }

    @GetMapping("/application/center/list/dictionary")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<ApplicationVo>> getApplicationListNoAuth(ApplicationQueryRequestV2 request) {
        return applicationService.getApplicationList(request);
    }

    @GetMapping("/console/application/center/app/info")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<ApplicationVo> getApplicationInfoWithAuth(
        @ApiParam(name = "id", value = "??????id") @RequestParam String id) {
        return applicationService.getApplicationInfo(id);
    }

    @GetMapping("/application/center/app/info/dictionary")
    @ApiOperation("????????????????????????")
    public Response<ApplicationVo> getApplicationInfoNoAuth(
        @ApiParam(name = "id", value = "??????id") String id
    ) {
        return applicationService.getApplicationInfo(id);
    }

    /**
     * ??????????????????
     *
     * @param vo ??????
     * @return ?????????
     */
    @PostMapping("/console/application/center/app/info")
    @ApiOperation("??????????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.BASIC_INFO,
        logMsgKey = BizOpConstants.Message.MESSAGE_BASIC_INFO_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response addApplication(@RequestBody ApplicationVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION, vo.getApplicationName());
        return applicationService.addApplication(vo);
    }

    @PutMapping("/console/application/center/app/info")
    @ApiOperation("????????????")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.BASIC_INFO,
        logMsgKey = BizOpConstants.Message.MESSAGE_BASIC_INFO_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response modifyApplication(@RequestBody ApplicationVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        return applicationService.modifyApplication(vo);
    }

    @ApiOperation("??????????????????")
    @DeleteMapping("/console/application/center/app/info")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.BASIC_INFO,
        logMsgKey = BizOpConstants.Message.MESSAGE_BASIC_INFO_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public Response deleteApplication(
        @RequestBody ApplicationVo vo) {
        if (vo == null || StringUtil.isEmpty(vo.getId())) {
            return Response.fail(FALSE_CODE, "id ????????????");
        }
        Response<ApplicationVo> applicationVo = applicationService.getApplicationInfo(vo.getId());
        if (null == applicationVo.getData().getId()) {
            return Response.fail("??????????????????");
        }
        ApplicationVo data = applicationVo.getData();
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION, data.getApplicationName());
        return applicationService.deleteApplication(vo.getId());
    }

    @ApiOperation("????????????????????????????????????")
    @GetMapping("/application/center/app/switch/calculate")
    public Response appSwitchForce(@RequestParam(value = "uid", required = false) Long uid) {
        TenantCommonExt tenantCommonExt = WebPluginUtils.traceTenantCommonExt();
        if(uid != null && !uid.equals(tenantCommonExt.getTenantId())) {
            return Response.fail("???????????????");
        }
        return applicationService.calculateUserSwitch(tenantCommonExt);
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("/application/center/app/config/url")
    public Response getExportDownloadConfigUrl(@ApiParam(name = "id", value = "??????id") @NotNull String id,
        HttpServletRequest request) {
        return applicationService.buildExportDownLoadConfigUrl(id, request);
    }

    @ApiOperation("|_ ??????????????????")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "??????id",
            paramType = "query", dataType = "int")
    })
    @GetMapping("/application/center/app/config/export")
    public void export(@RequestParam("id") Long applicationId, HttpServletResponse response) {
        applicationService.exportApplicationConfig(response, applicationId);
    }

    @ApiOperation("|_ ??????????????????")
    @PostMapping("/application/center/app/config/import")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "??????id",
            paramType = "form", dataType = "int"),
        @ApiImplicitParam(name = "file", value = "????????? excel",
            paramType = "form", dataType = "file")
    })
    public Response importApplicationConfig(@RequestParam MultipartFile file, @RequestParam Long id) {
        return applicationService.importApplicationConfig(file, id);
    }

    @ApiOperation("???????????????????????????????????????")
    @GetMapping("/application/center/app/config/ds/isnew")
    public Response<Boolean> appDsConfigIsNewVersion() {
        return applicationService.appDsConfigIsNewVersion();
    }

    @PostMapping("/application/center/unstallAllAgent")
    @ApiOperation("??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public void uninstallAllAgent(@RequestBody AppUninstallAgentVO vo) {
        applicationService.uninstallAllAgent(vo.getAppIds());
    }

    @PostMapping("/application/center/resumeAllAgent")
    @ApiOperation("??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public void resumeAllAgent(@RequestBody AppUninstallAgentVO vo) {
        applicationService.resumeAllAgent(vo.getAppIds());
    }

    @PostMapping("/application/center/operateCheck")
    @ApiOperation("????????????")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response<String> operateCheck(@RequestBody AppOperateAgentCheckVO vo) {
       return applicationService.operateCheck(vo.getAppIds(),vo.getOperate());

    }

    @ApiOperation("????????????????????????")
    @PutMapping("/application/center/app/switch/silence")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.PRESSURE_CONFIG_SWITCH,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SWITCH_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.CONFIG_CENTER,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response UpdateAppSilenceSwitch(@RequestBody ApplicationVo vo) {
        if (vo == null || vo.getSilenceEnable() == null) {
            return Response.fail(FALSE_CODE, "silenceEnable ????????????");
        }
        OperationLogContextHolder.operationType(
            vo.getSilenceEnable() ? BizOpConstants.OpTypes.OPEN : BizOpConstants.OpTypes.CLOSE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ACTION,
            vo.getSilenceEnable() ? BizOpConstants.OpTypes.OPEN : BizOpConstants.OpTypes.CLOSE);
        return applicationService.userAppSilenceSwitch(WebPluginUtils.traceTenantCommonExt(), vo.getSilenceEnable());
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("/application/center/app/switch/silence")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.CONFIG_CENTER,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response appSilenceSwitch() {
        return applicationService.userAppSilenceSwitchInfo();
    }

    @ApiOperation("?????????????????????????????????")
    @GetMapping("/application/center/app/report/config/info")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.CONFIG_CENTER,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response AppConfigReportInfo(@ApiParam(name = "bizType", value = "????????????") @NotNull Integer bizType,
        @ApiParam(name = "appName", value = "????????????") String appName) {
        return applicationService.getApplicationReportConfigInfo(bizType, appName);
    }

    /**
     * ????????????????????????
     *
     * @param request ?????????????????????????????????
     */
    @GetMapping("/application/center/app/monitorDetailes")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<ApplicationVisualInfoResponse>> getApplicationVisualInfo(
        @Valid ApplicationVisualInfoQueryRequest request) {
        return applicationService.getApplicationVisualInfo(request);
    }

    /**
     * ????????????????????????
     *
     * @param request ?????????????????????????????????
     */
    @PostMapping("/application/center/app/attendService")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public void attendApplicationService(@RequestBody ApplicationVisualInfoQueryRequest request) throws Exception {
        if (null == request.getAttend()) {
            return;
        }
        applicationService.attendApplicationService(request);
    }

    @GetMapping("/application/center/app/activityList")
    @ApiOperation("??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response getApplicationActivityList(@Valid ApplicationVisualInfoQueryRequest request) {
        Response<List<ApplicationVisualInfoResponse>> response = applicationService.getApplicationVisualInfo(request);
        List<ApplicationVisualInfoResponse> data = response.getData();
        if (CollectionUtils.isNotEmpty(data)) {
            Map allActiveIdAndName = data.get(0).getAllActiveIdAndName();
            ArrayList<Map.Entry> activityList = new ArrayList<>();
            if (null != allActiveIdAndName && !allActiveIdAndName.isEmpty()) {
                Set<Map.Entry> set = allActiveIdAndName.entrySet();
                set.forEach(entry -> activityList.add(entry));
            }
            return Response.success(activityList);
        }
        return null;
    }


    @PostMapping("/application/center/app/gotoActivityInfo")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Map<Long, Boolean> gotoActivityInfo(@Validated @RequestBody ActivityCreateRequest request) throws Exception {
        HashMap<Long, Boolean> result = new HashMap<>(1);
        BusinessLinkManageTableEntity entity = activityService.getActivity(request);
        if (Objects.nonNull(entity)) {
            result.put(entity.getLinkId(), true);
        } else {
            String temporaryKey = MD5Tool.getMD5(request.getApplicationName() + request.getLabel() + WebPluginUtils.traceTenantId() + WebPluginUtils.traceEnvCode());
            entity = activityService.getActivityByName(temporaryKey);
            if (null != entity) {
                result.put(entity.getLinkId(), false);
            }else {
                List<ServiceInfoDTO> applicationEntrances = applicationEntranceClient.getApplicationEntrances(
                    request.getApplicationName(), "",null,1,500);
                if (CollectionUtils.isNotEmpty(applicationEntrances)) {
                    List<ApplicationEntrancesResponse> responseList = applicationEntrances.stream()
                        .filter(item -> !item.getServiceName().startsWith("PT_"))
                        .map(item -> {
                            ApplicationEntrancesResponse applicationEntrancesResponse = new ApplicationEntrancesResponse();
                            applicationEntrancesResponse.setMethod(item.getMethodName());
                            applicationEntrancesResponse.setRpcType(item.getRpcType());
                            applicationEntrancesResponse.setExtend(item.getExtend());
                            applicationEntrancesResponse.setServiceName(item.getServiceName());
                            applicationEntrancesResponse.setLabel(
                                ActivityUtil.serviceNameLabel(item.getServiceName(), item.getMethodName()));
                            applicationEntrancesResponse.setValue(
                                ActivityUtil.createLinkId(item.getServiceName(), item.getMethodName(),
                                    item.getAppName(), item.getRpcType(), item.getExtend()));
                            return applicationEntrancesResponse;
                            // ????????????
                        }).distinct().filter(item -> item.getLabel().equals(request.getLabel())).collect(
                            Collectors.toList());
                    if (CollectionUtils.isNotEmpty(responseList)) {
                        request.setLinkId(responseList.get(0).getValue());
                    }
                }
            }
            request.setActivityName(temporaryKey);
            request.setRpcType(request.getRpcType());
            applicationService.gotoActivityInfo(request);
            result.put(activityService.getActivityByName(temporaryKey).getLinkId(), false);
        }
        return result;
    }
}
