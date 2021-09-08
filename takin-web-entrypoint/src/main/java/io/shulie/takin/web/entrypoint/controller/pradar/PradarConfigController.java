package io.shulie.takin.web.entrypoint.controller.pradar;

import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.pradar.PradarConfigService;
import io.shulie.takin.web.common.annocation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * pradar ZK开关、配置
 *
 * @author cyf
 * create: 2020-02-02
 */
@RequestMapping("/api/pradar/switch")
@Api(tags = "PradarSwitch", value = "pradar开关、配置")
@RestController
public class PradarConfigController {

    @Autowired
    PradarConfigService pradarConfigService;

    @ApiOperation("获取配置列表")
    @GetMapping("/list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRADAR_CONFIG,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<PradarZKConfigResponse> pageList(PradarZKConfigQueryRequest queryRequest) {
        PageUtils.clearPageHelper();
        queryRequest.setCurrent(queryRequest.getCurrent() + 1);
        return pradarConfigService.list(queryRequest);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.PUT, RequestMethod.POST})
    @ApiOperation("PRADAR配置修改")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRADAR,
        subModuleName = BizOpConstants.SubModules.PRADAR_CONFIG,
        logMsgKey = BizOpConstants.Message.PRADAR_CONFIG_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRADAR_CONFIG,
        needAuth = ActionTypeEnum.UPDATE
    )
    public void update(@Validated @RequestBody PradarZKConfigUpdateRequest request) {
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        pradarConfigService.updateConfig(request);
    }

    @PostMapping("/add")
    @ApiOperation("PRADAR配置新增")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRADAR,
        subModuleName = BizOpConstants.SubModules.PRADAR_CONFIG,
        logMsgKey = BizOpConstants.Message.PRADAR_CONFIG_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRADAR_CONFIG,
        needAuth = ActionTypeEnum.CREATE
    )
    public void add(@Validated @RequestBody PradarZKConfigCreateRequest request) {
        OperationLogContextHolder.operationType(OpTypes.CREATE);
        pradarConfigService.addConfig(request);
    }

    @DeleteMapping("/delete")
    @ApiOperation("PRADAR配置删除")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRADAR,
        subModuleName = BizOpConstants.SubModules.PRADAR_CONFIG,
        logMsgKey = Message.PRADAR_CONFIG_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRADAR_CONFIG,
        needAuth = ActionTypeEnum.DELETE
    )
    public void delete(@Validated @RequestBody PradarZKConfigDeleteRequest request) {
        OperationLogContextHolder.operationType(OpTypes.DELETE);
        pradarConfigService.deleteConfig(request);
    }
}
