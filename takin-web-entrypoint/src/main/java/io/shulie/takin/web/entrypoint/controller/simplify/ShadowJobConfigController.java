package io.shulie.takin.web.entrypoint.controller.simplify;

import java.util.Map;

import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.query.ShadowJobConfigQuery;
import com.pamirs.takin.entity.domain.vo.ShadowJobConfigVo;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.service.simplify.ShadowJobConfigService;
import io.shulie.takin.web.biz.utils.Estimate;
import io.shulie.takin.web.biz.utils.XmlUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-03-17 16:13
 */
@Api(tags = "影子JOB配置")
@Slf4j
@RequestMapping(ApiUrls.TAKIN_API_URL + "console")
@RestController
public class ShadowJobConfigController {

    @Autowired
    private ShadowJobConfigService shadowJobConfigService;

    @ApiOperation(value = "影子JOB配置分页查询, Owner: yuhan.tang")
    @GetMapping(value = ApiUrls.API_TAKIN_SIMPLIFY_SHADOW_QUERY_CONFIGS, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response queryByPage(@RequestParam(value = "pageSize", defaultValue = "0") Integer pageSize,
        @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
        @RequestParam(value = "orderBy", defaultValue = "") String orderBy,
        @RequestParam(value = "applicationId", defaultValue = "") Long applicationId) {
        try {
            Estimate.notBlank(applicationId, "应用ID不能为空");
            ShadowJobConfigQuery query = new ShadowJobConfigQuery();
            query.setPageSize(pageSize);
            query.setPageNum(pageNum);
            query.setOrderBy(orderBy);
            query.setApplicationId(applicationId);
            return shadowJobConfigService.queryByPage(query);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "影子JOB配置查询详情, Owner: yuhan.tang")
    @GetMapping(value = ApiUrls.API_TAKIN_SIMPLIFY_SHADOW_QUERY_DETAIL_CONFIGS,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response queryDetail(@RequestParam(value = "id") Long id) {
        try {
            Estimate.notBlank(id, "ID不能为空");
            return shadowJobConfigService.queryDetail(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "影子JOB配置新增, Owner: yuhan.tang")
    @PostMapping(value = ApiUrls.API_TAKIN_SIMPLIFY_SHADOW_INSERT_CONFIGS,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.JOB_TASK,
        logMsgKey = BizOpConstants.Message.MESSAGE_JOB_TASK_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Response insert(@RequestBody TShadowJobConfig config) {
        try {
            Estimate.notBlank(config.getApplicationId(), "应用ID不能为空");
            Estimate.notBlank(config.getConfigCode(), "相关配置不能为空");
            // 备注字段上限
            if (StringUtils.isNotBlank(config.getRemark()) && config.getRemark().length() > 256) {
                throw new TakinWebException(ExceptionCode.JOB_PARAM_ERROR, "备注长度不得超过256字符");
            }
            Map<String, String> xmlMap = XmlUtil.readStringXml(config.getConfigCode());
            String className = xmlMap.get("className");
            OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
            OperationLogContextHolder.addVars(BizOpConstants.Vars.TASK, className);

            return shadowJobConfigService.insert(config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "影子JOB配置修改, Owner: yuhan.tang")
    @RequestMapping(value = ApiUrls.API_TAKIN_SIMPLIFY_SHADOW_UPDATE_CONFIGS,
        method = {RequestMethod.PUT, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.JOB_TASK,
        logMsgKey = BizOpConstants.Message.MESSAGE_JOB_TASK_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Response update(@RequestBody ShadowJobConfigQuery query) {
        try {
            Estimate.notBlank(query.getId(), "ID不能为空");
            // 备注字段上限
            if (StringUtils.isNotBlank(query.getRemark()) && query.getRemark().length() > 256) {
                throw new TakinWebException(ExceptionCode.JOB_PARAM_ERROR, "备注长度不得超过256字符");
            }
            OperationLogContextHolder.operationType(OpTypes.UPDATE);
            Map<String, String> xmlMap = XmlUtil.readStringXml(query.getConfigCode());
            String className = xmlMap.get("className");
            OperationLogContextHolder.addVars(BizOpConstants.Vars.TASK, className);
            OperationLogContextHolder.addVars(BizOpConstants.Vars.TASKConfig, query.getConfigCode());
            shadowJobConfigService.update(query);
            return Response.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "影子JOB配置禁用停止, Owner: yuhan.tang")
    @PutMapping(value = ApiUrls.API_TAKIN_SIMPLIFY_SHADOW_UPDATE_STATUS_CONFIGS,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.JOB_TASK,
        logMsgKey = BizOpConstants.Message.MESSAGE_JOB_TASK_ENABLE_DISABLE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response updateStatus(@RequestBody ShadowJobConfigQuery query) {
        try {
            Estimate.notBlank(query.getId(), "ID不能为空");

            ShadowJobConfigVo shadowJobConfigVo = shadowJobConfigVo(query.getId());
            if (null == shadowJobConfigVo) {
                return Response.fail("影子JOB不存在");
            }
            OperationLogContextHolder.operationType(
                query.getStatus() == 0 ? BizOpConstants.OpTypes.ENABLE : BizOpConstants.OpTypes.DISABLE);
            OperationLogContextHolder.addVars(BizOpConstants.Vars.TASK, shadowJobConfigVo.getName());

            shadowJobConfigService.update(query);
            return Response.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "影子JOB配置删除, Owner: yuhan.tang")
    @DeleteMapping(value = ApiUrls.API_TAKIN_SIMPLIFY_SHADOW_DELETE_CONFIGS,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.JOB_TASK,
        logMsgKey = BizOpConstants.Message.MESSAGE_JOB_TASK_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public Response delete(@RequestBody ShadowJobConfigQuery query) {
        ShadowJobConfigVo shadowJobConfigVo = shadowJobConfigVo(query.getId());
        if (null == shadowJobConfigVo) {
            return Response.fail("影子JOB不存在");
        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.TASK, shadowJobConfigVo.getName());
        try {
            Estimate.notBlank(query.getId(), "ID不能为空");
            return shadowJobConfigService.delete(query.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }
    }

    private ShadowJobConfigVo shadowJobConfigVo(Long id) {
        Response<ShadowJobConfigVo> jobConfigVo = shadowJobConfigService.queryDetail(id);
        if (null == jobConfigVo) {
            return null;
        }
        ShadowJobConfigVo data = jobConfigVo.getData();
        return data;
    }

}
