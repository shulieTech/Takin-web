package io.shulie.takin.web.entrypoint.controller.v2.ds;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pamirs.attach.plugin.dynamic.one.Converter;
import com.pamirs.attach.plugin.dynamic.one.Type;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsTemplateQueryInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationDsV2Response;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 南风
 * @Date: 2021/8/27 1:56 下午
 */
@Slf4j
@RestController("v2.application.ds")
@RequestMapping(ApiUrls.TAKIN_API_URL+"v2")
@Api(tags = "接口-v2:影子库表管理", value = "影子库表管理")
public class DsController {

    @Autowired
    private DsService dsService;

    @ApiOperation("查询影子库表列表")
    @GetMapping("link/ds/manage")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<ApplicationDsV2Response> dsQuery(@RequestParam(value = "applicationId", required = true) Long applicationId) {
        return dsService.dsQueryV2(applicationId);
    }


    @ApiOperation("查询影子库表详情")
    @GetMapping("link/ds/manage/detail")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response<ShadowDetailResponse> dsQueryDetail(@RequestParam(value = "applicationId", required = true) Long applicationId,
                                                        @RequestParam(value = "recordId", required = true) Long recordId ,
                                                        @RequestParam(value = "isNewData",required = true ) Boolean isNewData,
                                                        @RequestParam(value = "middlewareType",required = true ) String middlewareType) {
        return dsService.dsQueryDetailV2(applicationId,recordId,middlewareType,isNewData);
    }

    @ApiOperation("获取影子库表隔离方案")
    @GetMapping("link/ds/manage/programme")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> dsQueryProgramme(@RequestParam(value = "plugName", required = true) String plugName,
                                        @RequestParam(value = "middlewareType",required = true ) String middlewareType) {
        return dsService.queryDsType(middlewareType,plugName);
    }


    @ApiOperation("编辑影子配置")
    @PostMapping("link/ds/config")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
            logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_UPDATE
    )
    public Response dsUpdateConfig(@RequestBody @Validated ApplicationDsUpdateInputV2 updateRequestV2) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(Vars.SHADOW_DATABASE_TABLE_JSON, JSON.toJSONString(updateRequestV2));
        return dsService.dsUpdateConfig(updateRequestV2);
    }

    @ApiOperation("获取影子库表样式模版")
    @PostMapping("link/ds/config/template")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response  dsQueryConfigTemplate(@RequestBody @Validated ApplicationDsTemplateQueryInputV2 inputV2) {
        return dsService.dsQueryConfigTemplate(inputV2.getAgentSourceType(),
                inputV2.getDsType(),inputV2.getIsNewData(),inputV2.getCacheType(),inputV2.getConnectionPool());
    }


    @ApiOperation("删除影子库表记录")
    @DeleteMapping("link/ds/config/delete")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.DELETE
    )
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
            logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_DELETE
    )
    public Response  dsDeleteDsConfig(@RequestBody @Validated ApplicationDsDeleteInputV2 inputV2) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SHADOW_DATABASE_TABLE_JSON, JSON.toJSONString(inputV2));
        dsService.dsDeleteV2(inputV2.getId(),inputV2.getMiddlewareType(),inputV2.getIsNewData(),inputV2.getApplicationId());
        return Response.success();
    }

    @ApiOperation("新增影子配置")
    @PostMapping("link/ds/config/create")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.CREATE
    )
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
            logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_CREATE
    )
    public Response dsCreateConfig(@RequestBody @Validated ApplicationDsCreateInputV2 createRequestV2) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SHADOW_DATABASE_TABLE_JSON, JSON.toJSONString(createRequestV2));
        return dsService.dsCreateConfig(createRequestV2);
    }

    @ApiOperation("新版本支持的中间件")
    @GetMapping("link/ds/support/new")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> dsSupportNew() {
        List<SelectVO> list = new ArrayList<>();
        /**
         * 过渡期使用,等全部中间件都支持了这就不要了
         */
        list.add(new SelectVO(Type.MiddleWareType.LINK_POOL.value(), Converter.TemplateConverter.TemplateEnum._1.getKey()));
        list.add(new SelectVO(Type.MiddleWareType.CACHE.value(), Converter.TemplateConverter.TemplateEnum._6.getKey()));
        return list;
    }

    @ApiOperation("新版本支持的中间件名称")
    @GetMapping("link/ds/support/name/new")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> dsSupportNameNew(@ApiParam(name = "middlewareType",value = "中间件类型",required = true)
                                               @RequestParam("middlewareType")
                                                       String middlewareType) {

        return  dsService.querySupperName(middlewareType);
    }

    @ApiOperation("获取缓存的模式")
    @GetMapping("link/ds/cache/type")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> dsCacheType() {

        return  dsService.queryCacheType();
    }

    @ApiOperation("启用禁用影子库表配置")
    @PutMapping("link/ds/enable")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
            logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_ENABLE_DISABLE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response enableConfig(@Validated @RequestBody ApplicationDsEnableInputV2 enableRequest) {
        OperationLogContextHolder.operationType(
                Integer.valueOf(0).equals(enableRequest.getStatus()) ? BizOpConstants.OpTypes.ENABLE
                        : BizOpConstants.OpTypes.DISABLE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SHADOW_DATABASE_TABLE_JSON, JSON.toJSONString(enableRequest));
        return dsService.enableConfigV2( enableRequest.getId(),enableRequest.getMiddlewareType(),
                enableRequest.getIsNewData(),enableRequest.getApplicationId(),enableRequest.getStatus());
    }

}
