package io.shulie.takin.web.entrypoint.controller.agent;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.entity.domain.vo.JarVersionVo;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import com.pamirs.takin.entity.domain.vo.TUploadNeedVo;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceVo;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.query.ShadowJobConfigQuery;
import io.shulie.takin.channel.bean.CommandPacket;
import io.shulie.takin.web.biz.utils.XmlUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.ConfCenterService;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.biz.service.UploadInterfaceService;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.service.simplify.ShadowJobConfigService;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationApiService;
import io.shulie.takin.web.data.param.application.AppDatabaseInputParam;
import io.shulie.takin.web.data.param.application.ConfigReportInputParam;
import io.shulie.takin.web.biz.service.perfomanceanaly.TraceManageService;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TODO
 */
@RestController
@RequestMapping(AgentUrls.PREFIX_URL)
@Api(tags = "接口: Agent全局配置")
@Slf4j
public class AgentPushController {

    @Resource
    private ApplicationService applicationService;
    @Resource
    private ApplicationApiService apiService;
    @Resource
    private ShadowJobConfigService shadowJobConfigService;
    @Resource
    private UploadInterfaceService uploadInterfaceService;
    @Resource
    private ConfCenterService confCenterService;
    @Resource
    private TraceManageService traceManageService;
    @Resource
    private ReportDetailService reportDetailService;

    @ApiOperation("|_ agent注册api")
    @PostMapping(value = AgentUrls.REGISTER_URL)
    public Response registerApi(@RequestBody Map<String, List<String>> register) {
        try {
            return apiService.registerApi(register);
        } catch (Exception e) {
            throw new TakinWebException(ExceptionCode.AGENT_INTERFACE_ERROR, "AgentController.registerApi 注册api异常", e);
        }
    }

    /**
     * @return 响应体
     */

    @PostMapping(value = AgentUrls.MIDDLE_STAUTS_URL)
    @ApiOperation("agent上传中间件列表状态")
    public Response uploadMiddlewareStatusAndRole(@RequestBody String requestJson,
                                                  @RequestParam(required = false, name = "appName") String appName) {
        try {
            Map<String, JarVersionVo> requestMap = JSONObject.parseObject(requestJson,
                    new TypeReference<Map<String, JarVersionVo>>() {
                    });
            return applicationService.uploadMiddlewareStatus(requestMap, appName);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail("middle status error");
        }
    }

    /**
     * 应用注册
     *
     * @param vo 入参
     * @return 响应体
     */
    @PostMapping(value = AgentUrls.APP_INSERT_URL)
    @ApiOperation("上传应用")
    public Response addApplication(@RequestBody ApplicationVo vo) {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION, vo.getApplicationName());
        return applicationService.addAgentRegisterApplication(vo);
    }

    /**
     * 接口需要有，防止大数据出现问题
     *
     * @return 响应体
     */

    @PostMapping(value = AgentUrls.UPLOAD_ACCESS_STATUS)
    @ApiOperation("上传应用状态")
    public Response uploadAccessStatus(@RequestBody NodeUploadDataDTO param) {
        return applicationService.uploadAccessStatus(param);
    }

    /**
     * 上传应用信息接口
     *
     * @param tUploadInterfaceVo appName与接口信息
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     */

    @PostMapping(value = AgentUrls.UPLOAD_APP_INFO,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("上传应用信息接口")
    public ResponseEntity<Object> judgeNeedUpload(@RequestBody TUploadInterfaceVo tUploadInterfaceVo) {
        try {
            return ResponseOk.create(uploadInterfaceService.saveUploadInterfaceData(tUploadInterfaceVo));
        } catch (Exception e) {
            throw new TakinWebException(ExceptionCode.AGENT_INTERFACE_ERROR, "AgentController.judgeNeedUpload 查询是否需要上传异常", e);
        }
    }

    /**
     * 判断是否需要上传
     *
     * @param uploadNeedVo appName与数量
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     */
    @PostMapping(value = AgentUrls.UPLOAD,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("判断是否需要上传")
    public ResponseEntity<Object> judgeNeedUpload(@RequestBody TUploadNeedVo uploadNeedVo) {
        try {
            return ResponseOk.create(uploadInterfaceService.executeNeedUploadInterface(uploadNeedVo));
        } catch (Exception e) {
            throw new TakinWebException(ExceptionCode.AGENT_INTERFACE_ERROR, "AgentController.judgeNeedUpload 查询是否需要上传异常", e);
        }
    }

    @ApiOperation(value = "影子JOB配置修改")
    @RequestMapping(value = AgentUrls.TAKIN_REPORT_ERROR_SHADOW_JOB_URL, method = {RequestMethod.PUT, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Response update(@RequestBody ShadowJobConfigQuery query) {
        try {
            if (query.getId() == null) {
                return Response.fail(TakinWebExceptionEnum.AGENT_UPDATE_SHADOW_JOB_VALIDATE_ERROR.getErrorCode(),
                        "ID不能为空");
            }

            Map<String, String> xmlMap = XmlUtil.readStringXml(query.getConfigCode());
            String className = xmlMap.get("className");
            OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
            OperationLogContextHolder.addVars(BizOpConstants.Vars.TASK, className);
            return shadowJobConfigService.update(query);
        } catch (Exception e) {
            return Response.fail(TakinWebExceptionEnum.AGENT_UPDATE_SHADOW_JOB_UPDATE_ERROR.getErrorCode(),
                    e.getMessage(), e);
        }
    }

    /**
     * 更新 应用agentVersion版本
     *
     * @return 响应体
     */
    @Deprecated
    @GetMapping(value = AgentUrls.AGENT_VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> appAgentVersionUpdate(@RequestParam("appName") String appName,
                                                        @RequestParam(value = "agentVersion") String agentVersion,
                                                        @RequestParam(value = "pradarVersion") String pradarVersion) {
        try {
            if (StringUtils.isBlank(agentVersion)
                    || StringUtils.isBlank(pradarVersion)
                    || "null".equalsIgnoreCase(agentVersion)
                    || "null".equalsIgnoreCase(pradarVersion)) {
                return ResponseError.create(1010100102, "更新应用版本异常,参数为空");
            }
            confCenterService.updateAppAgentVersion(appName, agentVersion, pradarVersion);
            return ResponseOk.create("succeed");
        } catch (Exception e) {
            throw new TakinWebException(ExceptionCode.AGENT_INTERFACE_ERROR, "AgentController.appAgentVersionUpdate 更新应用版本异常", e);
        }
    }

    /**
     * trace数据 方法追踪
     */
    @PostMapping(value = AgentUrls.PERFORMANCE_TRACE_URL)
    @ApiOperation(value = "agent上传trace信息")
    public void uploadTraceInfo(@RequestBody CommandPacket commandPacket) {
        traceManageService.uploadTraceInfo(commandPacket);
    }

    /**
     * 配置数据上报
     *
     * @param inputParam 入参
     */
    @PostMapping(value = AgentUrls.AGENT_PUSH_APPLICATION_CONFIG)
    @ApiOperation(value = "agent上传配置信息")
    public void uploadConfigInfo(@Validated @RequestBody ConfigReportInputParam inputParam) {
        reportDetailService.uploadConfigInfo(inputParam);
    }
}
