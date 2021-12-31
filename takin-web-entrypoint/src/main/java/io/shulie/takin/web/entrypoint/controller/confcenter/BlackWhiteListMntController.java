package io.shulie.takin.web.entrypoint.controller.confcenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinDictTypeEnum;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.entity.TBListDelete;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.query.BListQueryParam;
import com.pamirs.takin.entity.domain.query.TWListVo;
import com.pamirs.takin.entity.domain.vo.TApplicationInterface;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.ConfCenterService;
import io.shulie.takin.web.biz.utils.ExcelUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 说明: 黑白名单管理接口
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月13日
 */
@Api(tags = "黑白名单管理接口")
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class BlackWhiteListMntController {
    private final Logger LOGGER = LoggerFactory.getLogger(BlackWhiteListMntController.class);

    @Autowired
    private ConfCenterService confCenterService;


    /**
     * 说明: API.01.02.001 添加白名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_ADD_WLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("添加白名单接口")
    public ResponseEntity<Object> saveWhiteList(@RequestBody @Valid TWListVo twlistVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1010200101, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            List<String> list = confCenterService.saveWhiteList(twlistVo);
            if (CollectionUtils.isEmpty(list)) {
                return ResponseOk.create("succeed");
            } else {
                String join = Joiner.on(",").skipNulls().join(list);
                return ResponseOk.create(join + " 已经存在,请勿重新添加");
            }
        } catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_ADD_WLIST_EXCEPTION,"");
        }
    }

    /**
     * 白名单文件上传
     *
     * @return -
     */
    @ApiOperation("白名单文件上传")
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_UPLOAD_WLIST_URI)
    public ResponseEntity<Object> uploadWhiteList(@RequestParam(value = "file") MultipartFile[] files) {

        try {
            new ExcelUtil().verify(files);
            confCenterService.batchUploadWhiteList(files);
            return ResponseEntity.ok("success");
        } catch (TakinModuleException e) {
            return ResponseError.create(500, e.getErrorMessage());
        } catch (Exception e) {
            return ResponseError.create(500, e.getMessage());
        }
    }

    /**
     * 白名单导出接口
     *
     * @param applicationName 应用名
     * @param principalNo     负责人工号
     * @param type            白名单类型
     */
    @ApiOperation("白名单导出接口")
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_EXCEL_DOWNLOAD_URI)
    public void whiteListDownload(HttpServletResponse response,
                                  String applicationName,
                                  String principalNo,
                                  String type,
                                  String whiteListUrl,
                                  String whitelistIds) {
        try {
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("applicationName", applicationName);
            paramMap.put("principalNo", principalNo);
            paramMap.put("type", type);
            paramMap.put("whiteListUrl", whiteListUrl);
            if (StringUtils.isNotBlank(whitelistIds)) {
                paramMap.put("wlistIds", Arrays.asList(whitelistIds.split(",")));
            }
            ExcelUtil<TApplicationInterface> excelUtil = new ExcelUtil<>();
            excelUtil.export(response, confCenterService.queryWhiteListDownLoad(paramMap), null, "白名单管理");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 说明: API.01.02.002 查询白名单列表
     *
     * @return 成功, 则返回白名单列表, 失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @ApiOperation("查询白名单列表")
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_WLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryWhiteList(@RequestBody Map<String, Object> paramMap) {
        try {
            return ResponseOk.create(confCenterService.queryWhiteList(paramMap));
        } catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLIST_EXCEPTION,"");
        }
    }

    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_WLIST_4AGENT_URI,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseOk.ResponseResult queryWhiteList4Agent() {
        Map<String, List<Map<String, Object>>> result = new HashMap<>(0);
        try {
            result = confCenterService.queryBlackWhiteList("");
        } catch (Exception e) {
            LOGGER.error("BWListMntController.queryBList 查询白名单列表异常", e);
        }
        return ResponseOk.result(result);
    }

    /**
     * 说明: API.01.02.003 根据id查询白名单详情接口
     *
     * @return 成功, 则返回白名单详情, 失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @ApiOperation("根据id查询白名单详情接口")
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_WLISTBYID_URI,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> querySingleWhiteListById(@RequestParam("wlistId") String whitelistId) {
        if (StringUtils.isEmpty(whitelistId)) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYID_PARAMLACK,"");
        }
        try {
            return ResponseOk.create(confCenterService.querySingleWhiteListById(whitelistId));
        } catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYID_EXCEPTION,"");
        }
    }

    /**
     * 说明: API.01.02.004 根据id更新白名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @ApiOperation("根据id更新白名单接口")
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_UPDATE_WLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateWhiteListById(@RequestBody @Valid TWList tWhiteList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1010200401, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            confCenterService.updateWhiteListById(tWhiteList);
            return ResponseOk.create("succeed");
        }  catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_UPDATE_WLIST_EXCEPTION,"");
        }
    }

    /**
     * 说明: API.01.02.005 批量删除白名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @ApiOperation("批量删除白名单接口")
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_DELETE_WLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteWhiteListByIds(@RequestParam("wlistIds") String whitelistIds) {
        try {
            String disableDeleteWhitelistIdsList = confCenterService.deleteWhiteListByIds(whitelistIds);
            if (StringUtils.isNotEmpty(disableDeleteWhitelistIdsList)) {
                return ResponseError.create("该白名单{ " + disableDeleteWhitelistIdsList + " }在基础链路中使用,不允许删除");
            } else {
                return ResponseOk.create("succeed");
            }

        } catch (Exception e) {
            LOGGER.error("BWListMntController.deleteWListByIds  删除白名单信息异常", e);
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_DELETE_WLIST_EXCEPTION,"");
        }
    }

    /**
     * 说明: API.01.02.006 查询白名单字典列表接口
     *
     * @return 成功, 则返回白名单字典列表;失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @ApiOperation("查询白名单字典列表接口")
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_DIC_QUERY_WLIST_URI,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryWhitelistIdsListDic() {
        try {
            return ResponseOk.create(confCenterService.queryDicList(TakinDictTypeEnum.WLIST));
        } catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLISTDIC_EXCEPTION,"");
        }
    }

    /**
     * 说明: API.01.02.007 根据appname查询该应用下的白名单
     *
     * @param paramMap applicationName：应用名称
     *                 interfaceName：接口名称,当接口名称为空时,不进行模糊查询(非必填)
     *                 pageNum：当前页(必填)
     *                 pageSize：每页显示大小(必填)
     *                 type：接口类型(必填包含http,dubbo,job)
     *
     * @return 成功, 则返回白名单字典列表;失败则返回错误编码和错误信息
     * 响应结果：{
     * "code": 200,
     * "message": "succeed",
     * "data": {
     * "total": 100,
     * "data": [
     * "http://dpjjwms.pamirs.com/dpjjwms/76B060FA478E7934EC1BDBF7963FEF6C.cache.html"
     * ],
     * "pageSize": 10,
     * "type": "http",
     * "pageNum": 1,
     * "applicationName": "NVAS-vas-cas-web"
     * }
     * }
     *
     * @author shulie
     * @date 2019/3/1 14:42
     */
    @ApiOperation("根据appname查询该应用下的白名单")
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_WLISTBYAPPNAME_URI,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryWhitelistIdsListByAppName(@RequestBody Map<String, Object> paramMap) {
        try {
            return ResponseOk.create(confCenterService.queryWhiteListByAppName(paramMap));
        } catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYAPPNAME_EXCEPTION,"");
        }
    }

    /**
     * 说明: API.01.02.008 根据应用ID查询该应用下的白名单列表
     *
     * @param applicationId 应用ID
     *
     * @return 响应：
     * {
     * code: 200
     * data: [
     * {
     * wlistId: 604,
     * interfaceName: "com.test.pradar.service.DubboTestDemoService#test"
     * },
     * {
     * wlistId: 601,
     * interfaceName: "http://127.0.0.1:8080/pradar_test2/webservice/testDemoCxfService/testCxf"
     * }
     * ]
     * message: "succeed"
     * }
     *
     * @author shulie
     */
    @ApiOperation("根据应用ID查询该应用下的白名单列表")
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_WLISTBYAPPID_URI,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryWhitelistIdsListByAppId(@RequestParam("applicationId") String applicationId) {
        if (StringUtils.isBlank(applicationId)) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYAPPID_PARAM_EXCEPTION,"");
        }
        try {
            return ResponseOk.create(confCenterService.queryWhiteListByAppId(applicationId));
        } catch (Exception e) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYAPPID_EXCEPTION,"");
        }
    }

    //=============================== 黑名单管理  ===============================

    /**
     * 说明: API.01.02.007 添加黑名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @ApiOperation(value = "添加黑名单接口")
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_ADD_BLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BLACKLIST,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response saveBList(@RequestBody @Valid TBList tBList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.fail("0", bindingResult.getFieldError().getDefaultMessage());
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.REDIS_KEY, tBList.getRedisKey());
        try {
            confCenterService.saveBList(tBList);
            return Response.success();
        } catch (TakinWebException e) {
            LOGGER.error("ConfCenterController.queryWList 应用已经存在,请勿重新添加 {}", e);
            return Response.fail("0", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("BWListMntController.saveBList 新增黑名单异常{}", e);
            return Response.fail("0", TakinErrorEnum.CONFCENTER_ADD_BLIST_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.02.008 查询黑名单列表
     *
     * @return 成功, 则返回黑名单列表, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @ApiOperation(value = "查询黑名单列表")
    @RequestMapping(method = RequestMethod.GET, value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_BLIST_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BLACKLIST,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response queryBList(@ApiParam(name = "redisKey", value = "Redis Key名称") String redisKey,
        Integer current,
        Integer pageSize) {
        try {
            current = current + 1;
            BListQueryParam bListQueryParam = new BListQueryParam();
            bListQueryParam.setCurrentPage(current);
            bListQueryParam.setPageSize(pageSize);
            bListQueryParam.setRedisKey(redisKey);
            return confCenterService.queryBList(bListQueryParam);
        } catch (Exception e) {
            LOGGER.error("BWListMntController.queryBList 查询黑名单列表异常{}", e);
            return Response.fail(String.valueOf(TakinErrorEnum.CONFCENTER_QUERY_BLIST_EXCEPTION.getErrorCode()),
                TakinErrorEnum.CONFCENTER_QUERY_BLIST_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.02.009 根据id查询黑名单详情接口
     *
     * @return 成功, 则返回当个黑名单详情, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @ApiOperation(value = "根据id查询黑名单详情接口")
    @ApiImplicitParam(name = "blistId", value = "黑名单编号")
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_BLISTBYID_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BLACKLIST,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response querySingleBListById(@RequestParam("blistId") String blistId) {
        try {
            return Response.success(confCenterService.querySingleBListById(blistId));
        } catch (Exception e) {
            LOGGER.error("BWListMntController.querySingleBListById  根据id查询黑名单信息异常{}", e);
            return Response.fail("0", TakinErrorEnum.CONFCENTER_QUERY_SINGLEBLISTBYID_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.02.010 根据id更新黑名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @ApiOperation(value = "根据id更新黑名单接口")
    @PutMapping(value = ApiUrls.API_TAKIN_CONFCENTER_UPDATE_BLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BLACKLIST,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response updateBListById(@RequestBody @Valid TBList tBList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.fail("0", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.REDIS_KEY, tBList.getRedisKey());
        try {
            confCenterService.updateBListById(tBList);
            return Response.success();
        } catch (Exception e) {
            LOGGER.error("BWListMntController.updateBListById  根据id更新黑名单信息异常{}", e);
            return Response.fail("0", TakinErrorEnum.CONFCENTER_UPDATE_BLIST_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.02.010 根据id启用禁用黑名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @ApiOperation(value = "根据id启用禁用黑名单接口")
    @PutMapping(value = ApiUrls.API_TAKIN_CONFCENTER_USEYN_BLIST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BLACKLIST,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response updateStatusBListById(@RequestBody @Valid TBList tBList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Response.fail("0", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        TBList tbList = confCenterService.querySingleBListById(String.valueOf(tBList.getBlistId()));
        OperationLogContextHolder.operationType(
            "1".equals(tBList.getUseYn()) ? BizOpConstants.OpTypes.ENABLE : BizOpConstants.OpTypes.DISABLE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ACTION,
            "1".equals(tBList.getUseYn()) ? BizOpConstants.OpTypes.ENABLE : BizOpConstants.OpTypes.DISABLE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.REDIS_KEY, tbList.getRedisKey());
        try {
            confCenterService.updateBListById(tBList);
            return Response.success();
        } catch (Exception e) {
            LOGGER.error("BWListMntController.updateBListById  根据id更新黑名单信息异常{}", e);
            return Response.fail("0", TakinErrorEnum.CONFCENTER_UPDATE_BLIST_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.02.011 批量删除黑名单接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @ApiOperation(value = "批量删除黑名单接口")
    @DeleteMapping(value = ApiUrls.API_TAKIN_CONFCENTER_DELETE_BLIST_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.BLACKLIST,
        logMsgKey = BizOpConstants.Message.MESSAGE_BLACKLIST_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BLACKLIST,
        needAuth = ActionTypeEnum.DELETE
    )


    public Response deleteBListByIds(@RequestBody @Valid TBListDelete tbListDelete) {
        try {
            if (CollectionUtils.isEmpty(tbListDelete.getBlistIds())) {
                return Response.fail("0", "黑名单id不能为空");
            }
            List<TBList> tbListList = confCenterService.queryBListByIds(tbListDelete.getBlistIds());
            if (CollectionUtils.isEmpty(tbListList)) {
                return Response.success();
            }
            OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
            OperationLogContextHolder.addVars(BizOpConstants.Vars.REDIS_KEY,
                tbListList.stream().map(TBList::getRedisKey).collect(Collectors.joining(",")));
            confCenterService.deleteBListByIds(StringUtils.join(tbListDelete.getBlistIds().toArray(), ","));
            return Response.success();
        } catch (Exception e) {
            LOGGER.error("BWListMntController.deleteBListByIds  删除黑名单信息异常{}", e);
            return Response.fail("0", TakinErrorEnum.CONFCENTER_DELETE_BLIST_EXCEPTION.getErrorMessage());
        }
    }

    //=============================== 黑白名单列表 ===============================

    /**
     * 说明: API.01.02.012  无参查询黑白名单列表
     *
     * @return 成功, 返回黑白名单列表信息;失败,则返回错误编码和错误信息
     * @author shulie
     */
    // TODO 更新nginx查询
    //    @RequestMapping(value = ApiUrls.API_TRO_CONFCENTER_QUERY_BWLIST_URI,method = RequestMethod.GET, produces =
    //    MediaType.APPLICATION_JSON_VALUE)
    //    public ResponseEntity<Object> queryBWList(@RequestParam(name = "appName", required = false) String appName) {
    //        try {
    //
    //            Map<String, List<Map<String, Object>>> result = confCenterService.queryBWList(appName);
    //            return ResponseOk.create(result);
    //        } catch (TROModuleException e) {
    //            LOGGER.error("BWListMntController.queryBWList {}", e.getErrorMessage());
    //            return ResponseError.create(e.getErrorCode(),e.getErrorMessage());
    //        } catch (Exception e) {
    //            LOGGER.error("BWListMntController.queryBWList  查询黑白名单列表异常{}", e);
    //            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_BWLIST_EXCEPTION.getErrorCode(),
    //            TakinErrorEnum.CONFCENTER_QUERY_BWLIST_EXCEPTION.getErrorMessage());
    //        }
    //    }

    /**
     * 说明: API.01.02.013  无参查询黑白名单列表
     *
     * @return 成功, 返回黑白名单列表信息;失败,则返回错误编码和错误信息
     * @author shulie
     */
    //@RequestMapping(value = ApiUrls.API_TRO_CONFCENTER_QUERY_BWLISTMETRIC_URI, method = RequestMethod.GET,
    //    produces = MediaType.APPLICATION_JSON_VALUE)
    //public ResponseEntity<Object> queryBWMetricList(@RequestParam(name = "appName", required = false) String appName) {
    //    try {
    //        return ResponseOk.create(confCenterService.queryBWMetricList(appName));
    //    } catch (TROModuleException e) {
    //        LOGGER.error("BWListMntController.queryBWMetricList{}", e.getErrorMessage());
    //        return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
    //    } catch (Exception e) {
    //        LOGGER.error("BWListMntController.queryBWMetricList  查询Metric黑白名单列表异常{}", e);
    //        return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_BWLIST_EXCEPTION.getErrorCode(),
    //            TakinErrorEnum.CONFCENTER_QUERY_BWLIST_EXCEPTION.getErrorMessage());
    //    }
    //}
}
