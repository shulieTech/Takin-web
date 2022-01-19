package io.shulie.takin.web.entrypoint.controller.linkmanage;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListCreateListVO;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListOperateVO;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListQueryVO;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhiteListDeleteRequest;
import io.shulie.takin.web.biz.service.linkmanage.WhiteListService;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistUpdatePartAppNameInput;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhiteListUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhitelistUpdatePartAppNameRequest;
import io.shulie.takin.web.biz.pojo.response.whitelist.WhitelistStringResponse;
import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import io.shulie.takin.web.common.vo.whitelist.WhitelistPartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
 * @author mubai
 * @date 2020-04-20 17:39
 */

@Slf4j
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "接口: 白名单管理接口")
public class WhiteListConfigController {

    @Autowired
    private WhiteListService whiteListService;

    @ApiOperation("白名单添加接口")
    @PostMapping("/application/whitelist")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.WHITE_LIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITE_LIST_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response<Void> saveWhitelist(@RequestBody WhiteListCreateListVO vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE, vo.getInterfaceList() + "");
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE_TYPE,
            vo.getInterfaceType() == 1 ? "HTTP" : vo.getInterfaceType() == 2 ? "DUBBO" : "RABBITMQ");
        if (CollectionUtils.isEmpty(vo.getInterfaceList())) {
            return Response.success();
        }
        whiteListService.saveWhitelist(vo);
        return Response.success();
    }

    @ApiOperation("（批量）加入/取消白名单接口")
    @PutMapping("/application/whitelist")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.WHITE_LIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITE_LIST_ADD_REMOVE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response<Void> operateWhitelist(@RequestBody @Valid WhiteListOperateVO vo) {
        OperationLogContextHolder.operationType(
            vo.getType() == 0 ? BizOpConstants.OpTypes.REMOVE : BizOpConstants.OpTypes.ADD);
        List<String> interfaceName = new ArrayList<>();
        if (null != vo.getIds()) {
            vo.getIds().forEach(id -> {
                String[] splits = id.split("@@");
                interfaceName.add(splits[0]);
            });
        }
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE, interfaceName + "");
        whiteListService.operateWhitelist(vo);
        return Response.success();
    }

    @ApiOperation("查询白名单接口")
    @GetMapping("/application/whitelist")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<WhiteListVO>> listWhitelist(WhiteListQueryVO vo) {
        return Response.success(whiteListService.queryWhitelist(vo));
    }

    @PutMapping("/application/whitelist/update")
    @ApiOperation("编辑白名单接口")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.WHITE_LIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITE_LIST_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response<Void> updateWhitelist(@Validated @RequestBody WhiteListUpdateRequest request) {
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        whiteListService.updateWhitelist(request);
        return Response.success();
    }

    @ApiOperation("删除白名单接口")
    @DeleteMapping("/application/whitelist")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.WHITE_LIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITE_LIST_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public Response<Void> deleteWhitelist(@RequestBody @Valid WhiteListDeleteRequest request) {
        OperationLogContextHolder.operationType(OpTypes.DELETE);
        whiteListService.deleteWhitelist(request);
        return Response.success();
    }

    @ApiOperation("局部生效应用数据")
    @GetMapping("/application/part")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public WhitelistPartVO getPart(@RequestParam(value = "wlistId") Long wlistId) {
        return whiteListService.getPart(wlistId);
    }

    @ApiOperation("部分生效")
    @PostMapping("/application/part")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.WHITE_LIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITE_LIST_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public WhitelistStringResponse part(@RequestBody WhitelistUpdatePartAppNameRequest request) {
        WhitelistUpdatePartAppNameInput input = new WhitelistUpdatePartAppNameInput();
        BeanUtil.copyProperties(request, input);
        whiteListService.part(input);
        return new WhitelistStringResponse("生效应用保存成功");
    }

    @ApiOperation("全局生效")
    @GetMapping("/application/global")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.WHITE_LIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITE_LIST_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public WhitelistStringResponse global(@RequestParam("wlistId") Long wlistId) {
        whiteListService.global(wlistId);
        return new WhitelistStringResponse("全局生效成功");
    }

}
