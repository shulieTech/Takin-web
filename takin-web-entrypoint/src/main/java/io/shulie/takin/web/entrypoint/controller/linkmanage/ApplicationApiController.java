package io.shulie.takin.web.entrypoint.controller.linkmanage;

import java.util.Objects;

import com.pamirs.takin.entity.domain.vo.entracemanage.ApiCreateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.ApiDeleteVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.ApiUpdateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.EntranceApiVo;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.service.linkManage.ApplicationApiService;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.common.vo.application.ApplicationApiManageVO;
import io.swagger.annotations.Api;
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

/**
 * @author vernon
 * @date 2020/4/2 13:09
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
@Api(tags = "applicationApi", value = "应用api")
public class ApplicationApiController {

    @Autowired
    private ApplicationApiService apiService;

    //@ApiOperation("agent注册api")
    //@PostMapping(value = "/agent/api/register")
    //public Response registerApi(@RequestBody Map<String, List<String>> register) {
    //
    //    try {
    //        return apiService.registerApi(register);
    //
    //    } catch (Exception e) {
    //        return Response.fail(e.getMessage());
    //    }
    //}

    @ApiOperation("storm拉取api")
    @GetMapping(value = "/api/pull")
    public Response pull(@RequestParam(value = "appName", required = false) String appName) {
        try {
            return apiService.pullApi(appName);

        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation("查询")
    @GetMapping(value = "/api/get")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.ENTRYRULE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response query(@ApiParam(name = "applicationName", value = "应用名") String applicationName,
        @ApiParam(name = "api", value = "入口名") String api,
        Integer current,
        Integer pageSize) {
        EntranceApiVo vo = new EntranceApiVo();
        vo.setApplicationName(applicationName);
        vo.setApi(api);
        vo.setPageSize(pageSize);
        vo.setCurrentPage(current);
        try {
            return apiService.query(vo);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation("删除")
    @DeleteMapping(value = "/api/delete")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.ENTRYRULE,
        logMsgKey = BizOpConstants.Message.MESSAGE_ENTRYRULE_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.ENTRYRULE,
        needAuth = ActionTypeEnum.DELETE
    )
    public Response delete(@RequestBody ApiDeleteVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        Response<ApplicationApiManageVO> response = apiService.queryDetail(vo.getId());
        ApplicationApiManageVO apiManageDto = response.getData();
        if (Objects.isNull(apiManageDto)) {
            return Response.fail("入口规则不存在");
        }
        OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION_NAME, apiManageDto.getApplicationName());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ENTRY_API, apiManageDto.getApi());
        try {
            return apiService.delete(vo.getId());
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation("编辑")
    @PutMapping(value = "/api/update")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.ENTRYRULE,
        logMsgKey = BizOpConstants.Message.MESSAGE_ENTRYRULE_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.ENTRYRULE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response update(@RequestBody ApiUpdateVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        try {
            return apiService.update(vo);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate")) {
                return Response.fail("已经存在相同入口");
            }
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation("新增")
    @PostMapping(value = "/api/add")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.ENTRYRULE,
        logMsgKey = BizOpConstants.Message.MESSAGE_ENTRYRULE_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.ENTRYRULE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response add(@RequestBody ApiCreateVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION_NAME, vo.getApplicationName());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ENTRY_API, vo.getApi());
        // TODO: 2020/6/15
        try {
            return apiService.create(vo);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate")) {
                return Response.fail("不能重复添加");
            }
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation("详情")
    @GetMapping(value = "/api/getDetail")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.ENTRYRULE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response queryDetail(@RequestParam("id") String id) {
        try {
            return apiService.queryDetail(id);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }
}
