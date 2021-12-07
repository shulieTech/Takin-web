package io.shulie.takin.web.entrypoint.controller.linkmanage;

import java.util.Map;
import java.util.Set;

import com.pamirs.takin.common.enums.ds.DbTypeEnum;
import com.pamirs.takin.entity.domain.entity.simplify.AppBusinessTableInfo;
import com.pamirs.takin.entity.domain.query.agent.AppBusinessTableQuery;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanxx
 * @date 2020/3/12 下午3:10
 */
@Slf4j
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "影子库表管理", value = "影子库表管理")
public class DsController {

    private static final String DATABASE = "影子库 URL：";

    private static final String TABLE = "影子表 URL：";

    private static final String SERVER = "影子server URL：";

    @Autowired
    private DsService dsService;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 添加影子库表配置
     *
     * @return -
     */
    @ApiOperation("影子库表添加接口")
    @PostMapping("link/ds/manage")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_CREATE,
        opTypes = BizOpConstants.OpTypes.CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response dsAdd(@RequestBody ApplicationDsCreateInput createRequest) {
        return dsService.dsAdd(createRequest);
    }

    /**
     * 添加影子库表配置
     *
     * @return -
     */
    @ApiOperation("影子库表添加接口-老版本")
    @PostMapping("link/ds/manage/old")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_CREATE,
        opTypes = BizOpConstants.OpTypes.CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response dsAddOld(@RequestBody ApplicationDsCreateInput createRequest) {
        createRequest.setOldVersion(true);
        return this.dsAdd(createRequest);
    }

    /**
     * 初始化数据库加密配置
     *
     * @return -
     */
    @ApiOperation("加密数据库数据源配置")
    @PostMapping("link/ds/manage/secure/init")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_UPDATE,
        opTypes = OpTypes.UPDATE
    )
    public Response secureInit() {
        return dsService.secureInit();
    }

    /**
     * 查询影子库表配置
     *
     * @return -
     */
    @ApiOperation("查询影子库表配置")
    @GetMapping("link/ds/manage")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response dsQuery(@RequestParam(value = "applicationId", required = true) Long applicationId) {
        return dsService.dsQuery(applicationId);
    }

    @ApiOperation("影子库表配置详情")
    @GetMapping("link/ds/manage/detail")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response dsQueryDetail(@RequestParam(value = "id") Long dsId) {
        return dsService.dsQueryDetail(dsId, false);
    }

    /**
     * 影子库表配置详情
     *
     * @return -
     */
    @ApiOperation("影子库表配置详情-老版本")
    @GetMapping("link/ds/manage/detail/old")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response dsQueryDetailOld(@RequestParam(value = "id", required = true) Long id) {
        return dsService.dsQueryDetail(id, true);
    }

    /**
     * 修改影子库表配置
     *
     * @return -
     */
    @ApiOperation("修改影子库表配置")
    @PutMapping("link/ds/manage")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_UPDATE,
        opTypes = BizOpConstants.OpTypes.UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response dsUpdate(@RequestBody ApplicationDsUpdateInput updateRequest) {
        return dsService.dsUpdate(updateRequest);
    }

    /**
     * 修改影子库表配置
     *
     * @return -
     */
    @ApiOperation("修改影子库表配置-老版本")
    @PutMapping("link/ds/manage/old")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response dsUpdateOld(@RequestBody ApplicationDsUpdateInput updateRequest) {
        updateRequest.setOldVersion(true);
        return this.dsUpdate(updateRequest);
    }

    /**
     * 启用禁用影子库表配置
     *
     * @return -
     */
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
    public Response enableConfig(@RequestBody ApplicationDsEnableInput enableRequest) {
        OperationLogContextHolder.operationType(
            Integer.valueOf(0).equals(enableRequest.getStatus()) ? BizOpConstants.OpTypes.ENABLE
                : BizOpConstants.OpTypes.DISABLE);
        Response<ApplicationDsDetailOutput> response = dsService.dsQueryDetail(enableRequest.getId(), false);
        if (null == response) {
            return Response.fail("影子库表不存在");
        }
        ApplicationDsDetailOutput data = response.getData();
        return dsService.enableConfig(enableRequest);
    }

    /**
     * 删除影子库表配置
     *
     * @return -
     */
    @ApiOperation("删除影子库表配置")
    @RequestMapping(value = "link/ds/manage", method = RequestMethod.DELETE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_DATABASE_TABLE,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_DATABASE_TABLE_DELETE,
        opTypes = BizOpConstants.OpTypes.DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public Response dsDelete(@RequestBody ApplicationDsDeleteInput deleteRequest) {
        Response<ApplicationDsDetailOutput> response = dsService.dsQueryDetail(deleteRequest.getId(), false);
        if (!response.getSuccess()) {
            return response;
        }
        ApplicationDsDetailOutput data = response.getData();
        return dsService.dsDelete(deleteRequest);
    }

    @ApiOperation("agent上传影子表")
    @PostMapping(value = "link/ds/agent/report")
    public ResponseResult reportTable(@RequestBody Map<String, Set<String>> requestMap,
        @RequestParam("appName") String appName) {
        if (null == requestMap || requestMap.size() < 1) {
            return ResponseResult.fail("请求数据null", "请联系agent");
        }
        ApplicationDetailResult tApplicationMnt = applicationService.queryTApplicationMntByName(appName);
        if (null == tApplicationMnt) {
            return ResponseResult.fail("应用信息不存在", "请联系控制台");
        }

        try {
            for (Map.Entry<String, Set<String>> entry : requestMap.entrySet()) {
                StringBuilder tablesBuilder = new StringBuilder();
                String url = entry.getKey();
                for (String table : entry.getValue()) {
                    tablesBuilder.append(table).append(",");
                }

                String table = tablesBuilder.toString();
                if (table.contains(",")) {
                    table = table.substring(0, table.length() - 1);
                }
                ApplicationDsCreateInput createRequest = new ApplicationDsCreateInput();
                createRequest.setApplicationId(tApplicationMnt.getApplicationId());
                createRequest.setApplicationName(tApplicationMnt.getApplicationName());
                createRequest.setDbType(DbTypeEnum.DB.getCode());
                createRequest.setDsType(1);
                createRequest.setConfig(table);
                createRequest.setUrl(url);
                dsService.dsAdd(createRequest);
                AppBusinessTableInfo info = new AppBusinessTableInfo();
                info.setUrl(url);
                info.setTableName(table);
                info.setApplicationId(tApplicationMnt.getApplicationId());
                dsService.addBusiness(info);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.fail(e.getMessage(), "请联系控制台");
        }
        return ResponseResult.success();
    }

    @ApiOperation("查询agent上传业务表信息")
    @GetMapping(value = "link/ds/business/query")
    public Response queryPage(@RequestParam("pageSize") Integer pageSize,
        @RequestParam("pageNum") Integer pageNum,
        @RequestParam("applicationId") Long applicationId) {
        try {
            AppBusinessTableQuery query = new AppBusinessTableQuery();
            query.setPageNum(pageNum);
            query.setPageSize(pageSize);
            query.setApplicationId(applicationId);
            return dsService.queryPageBusiness(query);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

}
