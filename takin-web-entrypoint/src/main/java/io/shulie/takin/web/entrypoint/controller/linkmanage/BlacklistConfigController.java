package io.shulie.takin.web.entrypoint.controller.linkmanage;

import javax.validation.Valid;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.service.blacklist.BlacklistService;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistCreateInput;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistSearchInput;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistUpdateInput;
import io.shulie.takin.web.biz.pojo.output.blacklist.BlacklistOutput;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistBatchDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistBatchEnableRequest;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistCreateRequest;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistEnableRequest;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistSearchRequest;
import io.shulie.takin.web.biz.pojo.request.blacklist.BlacklistUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.blacklist.BlacklistDetailResponse;
import io.shulie.takin.web.biz.pojo.response.blacklist.BlacklistStringResponse;
import io.shulie.takin.web.common.enums.blacklist.BlacklistEnableEnum;
import io.shulie.takin.web.common.enums.blacklist.BlacklistTypeEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.vo.blacklist.BlacklistVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ???????????????
 *
 * @author ??????
 * @date 2021/4/6 1:50 ??????
 */
@Api(tags = "?????????????????????", value = "?????????")
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "application/blacklist")
public class BlacklistConfigController {

    @Autowired
    private BlacklistService blacklistService;

    /**
     * ??????
     *
     * @return -
     */
    @ApiOperation("?????????????????????")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public BlacklistStringResponse add(@RequestBody @Valid BlacklistCreateRequest request) {
        if (StringUtils.isBlank(request.getRedisKey()) || StringUtils.isBlank(request.getRedisKey().trim())) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_ADD_ERROR, "???????????????????????????");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        // ????????? redis
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BLACKLIST_TYPE, BlacklistTypeEnum.getDescByType(0));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BLACKLIST_VALUE, request.getRedisKey());
        BlacklistCreateInput input = new BlacklistCreateInput();
        BeanUtils.copyProperties(request, input);
        blacklistService.insert(input);
        return new BlacklistStringResponse("????????????");
    }

    /**
     * ??????
     *
     * @return -
     */
    @ApiOperation("???????????????????????????")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<BlacklistVO> pageList(BlacklistSearchRequest request) {
        BlacklistSearchInput input = new BlacklistSearchInput();
        BeanUtils.copyProperties(request, input);
        return blacklistService.pageList(input);
    }

    /**
     * ??????id??????
     *
     * @return -
     */
    @ApiOperation("??????id???????????????????????????")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public BlacklistDetailResponse querySingleById(@RequestParam("id") Long id) {
        BlacklistDetailResponse response = new BlacklistDetailResponse();
        BlacklistOutput output = blacklistService.selectById(id);
        if (output == null) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_SEARCH_ERROR, "????????????id");
        }
        BeanUtils.copyProperties(output, response);
        return response;
    }

    /**
     * ????????????id
     *
     * @return -
     */
    @ApiOperation("??????id?????????????????????")
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public BlacklistStringResponse update(@RequestBody BlacklistUpdateRequest request) {
        if (StringUtils.isBlank(request.getRedisKey()) || StringUtils.isBlank(request.getRedisKey().trim())) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_UPDATE_ERROR, "???????????????????????????");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        // ????????? redis
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BLACKLIST_TYPE, BlacklistTypeEnum.getDescByType(0));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BLACKLIST_VALUE, request.getRedisKey().trim());
        BlacklistUpdateInput input = new BlacklistUpdateInput();
        BeanUtils.copyProperties(request, input);
        // ????????????
        input.setRedisKey(input.getRedisKey().trim());
        blacklistService.update(input);
        return new BlacklistStringResponse("????????????");
    }

    /**
     * ????????????????????????
     *
     * @return -
     */
    @ApiOperation("??????id???????????????????????????")
    @PutMapping(value = "/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public BlacklistStringResponse enable(@RequestBody @Valid BlacklistEnableRequest request) {
        String operationType = BlacklistEnableEnum.ENABLE.getStatus().equals(request.getUseYn())
            ? BizOpConstants.OpTypes.ENABLE : BizOpConstants.OpTypes.DISABLE;
        OperationLogContextHolder.operationType(operationType);
        BlacklistUpdateInput input = new BlacklistUpdateInput();
        BeanUtils.copyProperties(request, input);
        blacklistService.enable(input);
        OperationLogContextHolder.addVars(Vars.ACTION, "?????????" + JSON.toJSONString(request));
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, "id = " + request.getBlistId());
        return new BlacklistStringResponse(operationType + "??????");
    }

    /**
     * ??????????????????
     *
     * @return -
     */
    @ApiOperation("?????????????????????????????????")
    @PutMapping(value = "list/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = Message.MESSAGE_BLACKLIST_BATCH_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public BlacklistStringResponse listEnable(@RequestBody BlacklistBatchEnableRequest request) {
        String operationType = BlacklistEnableEnum.ENABLE.getStatus().equals(request.getUseYn())
            ? BizOpConstants.OpTypes.ENABLE : BizOpConstants.OpTypes.DISABLE;
        blacklistService.batchEnable(request.getIds(), request.getUseYn());
        OperationLogContextHolder.operationType("??????"+operationType);
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, JSON.toJSONString(request));
        return new BlacklistStringResponse("??????" + operationType + "??????");
    }

    /**
     * ???????????????????????????
     *
     * @return -
     */
    @ApiOperation("???????????????????????????")
    @DeleteMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = Message.MESSAGE_BLACKLIST_BATCH_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public BlacklistStringResponse batchDelete(@RequestBody @Valid BlacklistBatchDeleteRequest request) {
        if (request.getIds() == null || request.getIds().size() == 0) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_DELETE_ERROR, "id??????????????????");
        }
        blacklistService.batchDelete(request.getIds());
        OperationLogContextHolder.operationType("??????"+BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, JSON.toJSONString(request));
        return new BlacklistStringResponse("??????????????????");

    }

    /**
     * ???????????????????????????
     *
     * @return -
     */
    @ApiOperation("???????????????????????????")
    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public BlacklistStringResponse delete(@RequestBody @Valid BlacklistDeleteRequest request) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        blacklistService.delete(request.getId());
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, JSON.toJSONString(request));
        return new BlacklistStringResponse("????????????");
    }

}
