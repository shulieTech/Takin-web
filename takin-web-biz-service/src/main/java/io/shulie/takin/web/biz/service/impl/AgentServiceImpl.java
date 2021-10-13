package io.shulie.takin.web.biz.service.impl;

import java.io.File;

import cn.hutool.core.util.StrUtil;
import io.shulie.takin.web.biz.pojo.request.agent.PushOperateRequest;
import io.shulie.takin.web.biz.pojo.response.agent.AgentApplicationNodeProbeOperateResponse;
import io.shulie.takin.web.biz.pojo.response.agent.AgentApplicationNodeProbeOperateResultResponse;
import io.shulie.takin.web.biz.service.AgentService;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.data.dao.ApplicationNodeProbeDAO;
import io.shulie.takin.web.data.dao.ProbeDAO;
import io.shulie.takin.web.data.param.probe.UpdateOperateResultParam;
import io.shulie.takin.web.data.result.application.ApplicationNodeProbeResult;
import io.shulie.takin.web.data.result.probe.ProbeDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/6/7 9:35 上午
 */
@Slf4j
@Service
public class AgentServiceImpl implements AgentService {

    @Value("${agent.interactive.takin.web.url:http://127.0.0.1:10008/takin-web}")
    private String takinWebUrl;

    @Autowired
    private ProbeDAO probeDAO;

    @Autowired
    private ApplicationNodeProbeDAO applicationNodeProbeDAO;

    @Override
    public AgentApplicationNodeProbeOperateResponse getOperateResponse(String applicationName, String agentId) {
        // 先查出该应用是否一键卸载, 如果是, 直接返回命令
        ApplicationNodeProbeResult applicationNodeProbeResult =
            applicationNodeProbeDAO.getByApplicationNameAndAgentId(applicationName, ProbeConstants.ALL_AGENT_ID);
        if (applicationNodeProbeResult == null) {
            // 如果该应用没有一键卸载过, 继续单个节点的查询
            // 根据应用名称, agentId 查出节点探针的操作记录
            applicationNodeProbeResult =
                applicationNodeProbeDAO.getByApplicationNameAndAgentId(applicationName, agentId);
        }

        if (applicationNodeProbeResult == null) {
            return null;
        }

        // 拼接参数, 返回
        AgentApplicationNodeProbeOperateResponse response = new AgentApplicationNodeProbeOperateResponse();
        response.setId(applicationNodeProbeResult.getOperateId());
        response.setCommandTime(applicationNodeProbeResult.getOperateId());
        response.setOperateType(applicationNodeProbeResult.getOperate());
        response.setDataPath(this.getProbeDownloadUrl(applicationName, agentId));
        return response;
    }

    @Override
    public File getFile(String applicationName, String agentId) {
        // 根据应用名称, agentId 查出节点探针的操作记录
        ApplicationNodeProbeResult applicationNodeProbeResult =
            applicationNodeProbeDAO.getByApplicationNameAndAgentId(applicationName, agentId);
        this.isGetFileError(applicationNodeProbeResult == null, "操作记录不存在!");

        // 根据操作类型, 获取文件
        if (ApplicationNodeProbeUtil.isUninstallOperate(applicationNodeProbeResult.getOperate())) {
            return null;
        }

        // 根据 probeId 获取文件路径
        ProbeDetailResult probe = probeDAO.getById(applicationNodeProbeResult.getProbeId());
        this.isGetFileError(probe == null, "探针包不存在!");
        return new File(probe.getPath());
    }

    @Override
    public AgentApplicationNodeProbeOperateResultResponse updateOperateResult(PushOperateRequest pushOperateRequest) {
        log.info("探针操作结果上报 --> 入参: {}", JsonUtil.bean2Json(pushOperateRequest));

        // 查出操作记录
        // 根据应用名称, agentId 查出节点探针的操作记录
        String appName = pushOperateRequest.getAppName();
        String agentId = pushOperateRequest.getAgentId();
        ApplicationNodeProbeResult applicationNodeProbeResult =
            applicationNodeProbeDAO.getByApplicationNameAndAgentId(appName, agentId);

        log.info("探针操作结果上报 --> 查询操作记录!");
        if (applicationNodeProbeResult == null) {
            throw new TakinWebException(ExceptionCode.AGENT_APPLICATION_NODE_PROBE_UPDATE_OPERATE_RESULT_ERROR,
                "操作记录不存在!");
        }

        log.info("探针操作结果上报 --> 更新操作记录!");
        AgentApplicationNodeProbeOperateResultResponse response = new AgentApplicationNodeProbeOperateResultResponse();
        // 比对操作结果, 如果是一样, 且是成功, 就不往下了
        Integer operateResult = pushOperateRequest.getOperateResult();
        if (operateResult.equals(applicationNodeProbeResult.getOperateResult())
            && operateResult == ProbeConstants.PROBE_OPERATE_RESULT_SUCCESS) {
            response.setBusinessCode(AppConstants.YES);
            return response;
        }

        // 一样, 返回成功, 不一样, 更新
        UpdateOperateResultParam updateOperateResultParam = new UpdateOperateResultParam();
        updateOperateResultParam.setOperateResult(operateResult);
        updateOperateResultParam.setId(applicationNodeProbeResult.getId());
        String errorMsg = pushOperateRequest.getErrorMsg();
        if (StrUtil.isNotBlank(errorMsg) && errorMsg.length() > 400) {
            errorMsg = errorMsg.substring(0, 400);
            updateOperateResultParam.setRemark(errorMsg);
        }

        // 这里不更新探针状态, 列表中使用 amdb 的状态, 操作中, 更新状态
        if (!applicationNodeProbeDAO.updateById(updateOperateResultParam)) {
            response.setBusinessCode(AppConstants.NO);
            response.setMessage("数据库更新失败, 请重试!");
        } else {
            response.setBusinessCode(AppConstants.YES);
        }

        return response;
    }

    /**
     * 探针包下载地址
     *
     * @param applicationName 应用名称
     * @param agentId         agentId
     * @return 下载地址
     */
    private String getProbeDownloadUrl(String applicationName, String agentId) {
        return String.format("%s%s%sfile?appName=%s&agentId=%s",
            takinWebUrl, AgentUrls.PREFIX_URL, AgentUrls.AGENT_APPLICATION_NODE_PROBE,
            applicationName, agentId);
    }

    /**
     * 是 获取探针包错误
     *
     * @param condition 错误条件
     * @param message   错误信息
     */
    private void isGetFileError(boolean condition, String message) {
        if (condition) {
            throw new TakinWebException(ExceptionCode.AGENT_APPLICATION_NODE_PROBE_GET_FILE_ERROR, message);
        }
    }

}
