package io.shulie.takin.web.entrypoint.controller.confcenter;

import com.github.pagehelper.util.StringUtil;
import com.pamirs.takin.entity.domain.query.ApplicationQueryParam;
import com.pamirs.takin.entity.domain.vo.AppUnstallAgentVo;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationVisualInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntrancesResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @author liuchuan
 * @date 2020-03-16 14:55
 */

@RestController("confApplicationController")
@RequestMapping(APIUrls.TAKIN_API_URL)
@Api(tags = "接口: 应用管理中心", value = "应用管理中心")
public class ApplicationController {

    private static String FALSE_CODE = "0";
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationEntranceClient applicationEntranceClient;
    @Autowired
    private ActivityService activityService;

    @GetMapping("/application/center/list")
    @ApiOperation("应用列表查询接口")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<ApplicationVo>> getApplicationListWithAuth(
        @ApiParam(name = "applicationName", value = "系统名字") String applicationName,
        @RequestParam(defaultValue = "0") Integer current,
        Integer pageSize,
        @ApiParam(name = "accessStatus", value = "接入状态") Integer accessStatus
    ) {
        current = current + 1;
        ApplicationQueryParam param = new ApplicationQueryParam();
        param.setCurrentPage(current);
        param.setPageSize(pageSize);
        param.setApplicationName(applicationName);
        return applicationService.getApplicationList(param, accessStatus);
    }

    @GetMapping("/application/center/list/dictionary")
    @ApiOperation("应用列表查询接口")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<ApplicationVo>> getApplicationListNoAuth(
    ) {
        return applicationService.getApplicationList();
    }

    @GetMapping("/console/application/center/app/info")
    @ApiOperation("应用详情查询接口")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<ApplicationVo> getApplicationInfoWithAuth(@ApiParam(name = "id", value = "系统id") @RequestParam String id) {
        return applicationService.getApplicationInfo(id);
    }

    @GetMapping("/application/center/app/info/dictionary")
    @ApiOperation("应用详情查询接口")
    public Response<ApplicationVo> getApplicationInfoNoAuth(
        @ApiParam(name = "id", value = "系统id") String id
    ) {
        return applicationService.getApplicationInfo(id);
    }

    @PostMapping("/console/application/center/app/info")
    @ApiOperation("新增应用接口")
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
    @ApiOperation("编辑应用")
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

    @ApiOperation("删除应用接口")
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
            return Response.fail(FALSE_CODE, "id 不能为空");
        }
        Response<ApplicationVo> applicationVo = applicationService.getApplicationInfo(vo.getId());
        if (null == applicationVo.getData().getId()) {
            return Response.fail("该应用不存在");
        }
        ApplicationVo data = applicationVo.getData();
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION, data.getApplicationName());
        return applicationService.deleteApplication(vo.getId());
    }

    @ApiOperation("压测开关状态重新计算接口")
    @GetMapping("/application/center/app/switch/calculate")
    public Response appSwitchForce(@RequestParam(value = "uid", required = false) Long uid) {
        return applicationService.calculateUserSwitch(uid);
    }

    @ApiOperation("获取下载导出配置地址")
    @GetMapping("/application/center/app/config/url")
    public Response getExportDownloadConfigUrl(@ApiParam(name = "id", value = "系统id") @NotNull String id,
        HttpServletRequest request) {
        return applicationService.buildExportDownLoadConfigUrl(id, request);
    }

    @ApiOperation("|_ 应用配置导出")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "应用id",
            paramType = "query", dataType = "int")
    })
    @GetMapping("/application/center/app/config/export")
    public void export(@RequestParam("id") Long applicationId, HttpServletResponse response) {
        applicationService.exportApplicationConfig(response, applicationId);
    }

    @ApiOperation("|_ 应用配置导入")
    @PostMapping("/application/center/app/config/import")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "应用id",
            paramType = "form", dataType = "int"),
        @ApiImplicitParam(name = "file", value = "导入的 excel",
            paramType = "form", dataType = "file")
    })
    public Response importApplicationConfig(@RequestParam MultipartFile file, @RequestParam Long id) {
        return applicationService.importApplicationConfig(file, id);
    }

    @ApiOperation("应用数据源配置是否为新版本")
    @GetMapping("/application/center/app/config/ds/isnew")
    public Response<Boolean> appDsConfigIsNewVersion() {
        return applicationService.appDsConfigIsNewVersion();
    }

    @PostMapping("/application/center/unstallAllAgent")
    @ApiOperation("一键卸载探针")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public void unstallAllAgent(@RequestBody AppUnstallAgentVo vo) {
        applicationService.uninstallAllAgent(vo.getAppIds());
    }

    @PostMapping("/application/center/resumeAllAgent")
    @ApiOperation("一键恢复探针")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public void resumeAllAgent(@RequestBody AppUnstallAgentVo vo) {
        applicationService.resumeAllAgent(vo.getAppIds());
    }


    @ApiOperation("编辑静默开关接口")
    @PutMapping("/application/center/app/switch/silence")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.CONFIG_CENTER,
            subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SWITCH,
            logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SWITCH_ACTION
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SWITCH,
            needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response UpdateAppSilenceSwitch(
            @RequestBody ApplicationVo vo) {
        if (vo == null || vo.getSilenceEnable()== null) {
            return Response.fail(FALSE_CODE, "silenceEnable 不能为空");
        }
        OperationLogContextHolder.operationType(
                vo.getSilenceEnable() ? BizOpConstants.OpTypes.OPEN : BizOpConstants.OpTypes.CLOSE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ACTION,
                vo.getSilenceEnable() ? BizOpConstants.OpTypes.OPEN : BizOpConstants.OpTypes.CLOSE);
        return applicationService.userAppSilenceSwitch(null, vo.getSilenceEnable());
    }

    @ApiOperation("获取静默开关状态接口")
    @GetMapping("/application/center/app/switch/silence")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.CONFIG_CENTER,
            subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SWITCH,
            logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SWITCH_ACTION
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SWITCH,
            needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response AppSilenceSwitch() {
        return applicationService.userAppSilenceSwitchInfo();
    }

    @ApiOperation("按租户查询上报数据接口")
    @GetMapping("/application/center/app/report/config/info")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.CONFIG_CENTER,
            subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SWITCH,
            logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SWITCH_ACTION
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SWITCH,
            needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response AppConfigReportInfo(@ApiParam(name = "bizType", value = "业务类型") @NotNull Integer bizType,
                                        @ApiParam(name = "appName", value = "应用名称") String appName) {
        return applicationService.getApplicationReportConfigInfo(bizType,appName);
    }


    /**
     * 应用监控查询接口
     *
     * @param request 包含应用名称及服务名称
     */
    @GetMapping("/application/center/app/monitorDetailes")
    @ApiOperation("应用监控查询接口")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<ApplicationVisualInfoResponse>> getApplicationVisualInfo(@Valid ApplicationVisualInfoQueryRequest request) {
        return applicationService.getApplicationVisualInfo(request);
    }

    /**
     * 关注应用服务接口
     *
     * @param request 包含应用名称及服务名称
     */
    @PostMapping("/application/center/app/attendService")
    @ApiOperation("关注应用服务接口")
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

    @PostMapping("/application/center/app/gotoActivityInfo")
    @ApiOperation("跳转业务活动详情")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.CREATE
    )
    public Map gotoActivityInfo(@Validated @RequestBody ActivityCreateRequest request) throws Exception {
        HashMap result = new HashMap();
        //业务活动名称模糊搜索
        String key = MD5Tool.getMD5(request.getApplicationName() + request.getLabel() + WebPluginUtils.getCustomerId());
        BusinessLinkManageTableEntity entity = activityService.getActivityByName(key);
        if (null != entity) {
            result.put(entity.getLinkId(), false);
        } else {
            entity = activityService.getActivity(request);
            if (null != entity) {
                result.put(entity.getLinkId(), true);
            }
            List<ServiceInfoDTO> applicationEntrances = applicationEntranceClient.getApplicationEntrances(
                    request.getApplicationName(), "");
            ApplicationEntrancesResponse entrancesResponse = applicationEntrances.stream()
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
                        // 增加去重
                    }).distinct().filter(item -> item.getLabel().equals(request.getLabel())).collect(Collectors.toList()).get(0);

            request.setActivityName(key);
            request.setLinkId(entrancesResponse.getValue());
            request.setRpcType(entrancesResponse.getRpcType());
            applicationService.gotoActivityInfo(request);
            result.put(activityService.getActivityByName(key).getLinkId(), false);
        }
        return result;
    }

}
