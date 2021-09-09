package io.shulie.takin.web.entrypoint.controller.confcenter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import com.github.pagehelper.util.StringUtil;
import com.pamirs.takin.entity.domain.query.ApplicationQueryParam;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/application/center/list")
    @ApiOperation("应用列表查询接口")
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
    public Response<List<ApplicationVo>> getApplicationListNoAuth(
    ) {
        return applicationService.getApplicationList();
    }

    @GetMapping("/console/application/center/app/info")
    @ApiOperation("应用详情查询接口")
    public Response<ApplicationVo> getApplicationInfoWithAuth(
        @ApiParam(name = "id", value = "系统id") String id
    ) {
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

}
