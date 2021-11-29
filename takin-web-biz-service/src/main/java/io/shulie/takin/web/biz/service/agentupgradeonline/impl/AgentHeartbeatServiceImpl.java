package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandResBO;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentHeartbeatRequest;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.agentcommand.IAgentCommandProcessor;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentHeartbeatService;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.biz.service.agentupgradeonline.WebApplicationPluginUpgradeService;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentUpgradeEnum;
import io.shulie.takin.web.common.enums.excel.BooleanEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.ProbeStatusEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.ApplicationPluginUpgradeDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Description agent心跳处理器
 * @Author ocean_wll
 * @Date 2021/11/11 2:53 下午
 */
@Service
public class AgentHeartbeatServiceImpl implements AgentHeartbeatService {

    private final Logger distributionLog = LoggerFactory.getLogger("AGENT_COMMAND_DISTRIBUTION");

    private final Logger reportLog = LoggerFactory.getLogger("AGENT_COMMAND_REPORT");

    /**
     * 企业版标识
     */
    private final static String ENTERPRISE_FLAG = "shulieEnterprise";

    @Resource
    private AgentReportService agentReportService;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private WebApplicationPluginUpgradeService applicationPluginUpgradeService;

    @Resource
    private ThreadPoolExecutor agentHeartbeatThreadPool;

    /**
     * 非企业版处理器集合
     */
    private final Map<Long, IAgentCommandProcessor> commandProcessorMap = new HashMap<>();

    public AgentHeartbeatServiceImpl(List<IAgentCommandProcessor> processorList) {
        processorList.forEach(processor -> commandProcessorMap.put(processor.getCommand().getCommand(), processor));
    }

    public List<AgentCommandResBO> process(AgentHeartbeatRequest commandRequest) {

        // 是否企业版
        boolean isEnterprise = ENTERPRISE_FLAG.equals(commandRequest.getFlag());
        Map<Long, IAgentCommandProcessor> processorMap = isEnterprise ? getEnterpriseCommandProcessorMap()
            : commandProcessorMap;

        // 检测状态
        AgentHeartbeatBO agentHeartbeatBO = buildAgentHeartBeatBO(commandRequest);

        // 异步处理上报的命令数据
        if (!CollectionUtils.isEmpty(commandRequest.getCommandResult())) {
            reportLog.info("projectName:{}, agentId:{}, commands:{}", agentHeartbeatBO.getProjectName(),
                agentHeartbeatBO.getAgentId(), commandRequest.getCommandResult());
            commandRequest.getCommandResult().forEach(commandResult ->
                agentHeartbeatThreadPool.execute(() -> {
                    IAgentCommandProcessor processor = processorMap.get(commandResult.getId());
                    if (processor != null) {
                        processor.process(agentHeartbeatBO, commandResult);
                    }
                })
            );
        }

        CreateAgentReportParam createAgentReportParam = new CreateAgentReportParam();
        BeanUtils.copyProperties(agentHeartbeatBO, createAgentReportParam);
        createAgentReportParam.setApplicationName(agentHeartbeatBO.getProjectName());
        createAgentReportParam.setStatus(agentHeartbeatBO.getCurStatus().getVal());

        // 数据入库
        agentReportService.insertOrUpdate(createAgentReportParam);

        List<Future<AgentCommandResBO>> futures = new ArrayList<>();

        // 异步处理各种命令
        for (Map.Entry<Long, IAgentCommandProcessor> entry : processorMap.entrySet()) {
            Future<AgentCommandResBO> future = agentHeartbeatThreadPool.submit(
                () -> entry.getValue().dealHeartbeat(agentHeartbeatBO));
            futures.add(future);
        }
        List<AgentCommandResBO> commandBOList = new ArrayList<>();
        futures.forEach(future -> {
            try {
                AgentCommandResBO agentCommandBO = future.get(60, TimeUnit.SECONDS);
                if (agentCommandBO != null) {
                    commandBOList.add(agentCommandBO);
                }
            } catch (Exception e) {
                // ignore
            }
        });

        List<AgentCommandResBO> result = filterCommand(commandBOList);

        if (!CollectionUtils.isEmpty(result)) {
            distributionLog.info("projectName:{}, agentId:{}, commands:{}", agentHeartbeatBO.getProjectName(),
                agentHeartbeatBO.getAgentId(), result);
        }

        return result;
    }

    /**
     * 过滤指令，有些指令不能同时返回
     *
     * @param commandBOList 指令集合
     * @return AgentCommandBO集合
     */
    private List<AgentCommandResBO> filterCommand(List<AgentCommandResBO> commandBOList) {
        boolean haveAgentGetFileCommand = false;
        boolean haveReportAgentUploadPathStatusCommand = false;
        for (AgentCommandResBO command : commandBOList) {
            if (AgentCommandEnum.AGENT_START_GET_FILE.getCommand().equals(command.getId())) {
                haveAgentGetFileCommand = true;
            }
            if (AgentCommandEnum.REPORT_AGENT_UPLOAD_PATH_STATUS.getCommand().equals(command.getId())) {
                haveReportAgentUploadPathStatusCommand = true;
            }
        }

        List<AgentCommandResBO> result = new ArrayList<>();
        // 如果有 200000 则不允许有 100110 和 100100
        // 如果有 100100 就不允许有 100110
        for (AgentCommandResBO command : commandBOList) {
            if (haveAgentGetFileCommand
                && (AgentCommandEnum.REPORT_AGENT_UPLOAD_PATH_STATUS.getCommand().equals(command.getId())
                || AgentCommandEnum.REPORT_UPGRADE_RESULT.getCommand().equals(command.getId()))) {
                continue;
            }
            if (haveReportAgentUploadPathStatusCommand
                && AgentCommandEnum.REPORT_UPGRADE_RESULT.getCommand().equals(command.getId())) {
                continue;
            }
            result.add(command);
        }

        return result;
    }

    /**
     * 获取agent状态
     *
     * @param agentHeartBeatBO 心跳数据
     * @return AgentReportStatusEnum
     */
    private AgentReportStatusEnum getAgentReportStatus(AgentHeartbeatBO agentHeartBeatBO) {
        // 判断是否已卸载  探针上报已卸载
        if (BooleanEnum.TRUE.getCode().equals(agentHeartBeatBO.getUninstallStatus())) {
            return AgentReportStatusEnum.UNINSTALL;
        }

        // 判断是否已休眠 探针上报已休眠
        if (BooleanEnum.TRUE.getCode().equals(agentHeartBeatBO.getDormantStatus())) {
            return AgentReportStatusEnum.SLEEP;
        }

        // 判断是否为启动中，agent状态为成功，simulator状态为null或者空字符串
        if (AgentStatusEnum.INSTALLED.getCode().equals(agentHeartBeatBO.getAgentStatus()) && StringUtils.isEmpty(
            agentHeartBeatBO.getSimulatorStatus())) {
            return AgentReportStatusEnum.STARTING;
        }

        // 判断是否为刚启动，agent状态为未安装，simulator状态为null或者空字符串
        if (AgentStatusEnum.UNINSTALL.getCode().equals(agentHeartBeatBO.getAgentStatus()) && StringUtils.isEmpty(
            agentHeartBeatBO.getSimulatorStatus())) {
            return AgentReportStatusEnum.BEGIN;
        }

        // 判断是否异常 agent状态异常 或 simulator状态不是安装成功
        if (AgentStatusEnum.INSTALL_FAILED.getCode().equals(agentHeartBeatBO.getAgentStatus())
            || !ProbeStatusEnum.INSTALLED.getCode().equals(agentHeartBeatBO.getSimulatorStatus())) {
            return AgentReportStatusEnum.ERROR;
        }

        // 查询当前应用的升级单批次
        if (AgentStatusEnum.INSTALLED.getCode().equals(agentHeartBeatBO.getAgentStatus())
            && ProbeStatusEnum.INSTALLED.getCode().equals(agentHeartBeatBO.getSimulatorStatus())) {

            // 查询最新的升级单
            ApplicationPluginUpgradeDetailResult pluginUpgradeDetailResult
                = applicationPluginUpgradeService.queryLatestUpgradeByAppIdAndStatus(
                agentHeartBeatBO.getApplicationId(), AgentUpgradeEnum.UPGRADE_SUCCESS.getVal());

            if (pluginUpgradeDetailResult == null
                || pluginUpgradeDetailResult.getUpgradeBatch().equals(agentHeartBeatBO.getCurUpgradeBatch())) {
                return AgentReportStatusEnum.RUNNING;
            } else {
                return AgentReportStatusEnum.WAIT_RESTART;
            }
        }

        return AgentReportStatusEnum.UNKNOWN;
    }

    /**
     * AgentCommandRequest -> AgentHeartBeatBO
     *
     * @param commandRequest AgentCommandRequest对象
     * @return AgentHeartBeatBO对象
     */
    private AgentHeartbeatBO buildAgentHeartBeatBO(AgentHeartbeatRequest commandRequest) {
        TApplicationMnt applicationMnt = applicationService.queryTApplicationMntByName(commandRequest.getProjectName());
        if (applicationMnt == null) {
            throw new TakinWebException(ExceptionCode.AGENT_REGISTER_ERROR, "应用名不存在");
        }

        AgentHeartbeatBO agentHeartbeatBO = new AgentHeartbeatBO();
        agentHeartbeatBO.setApplicationId(applicationMnt.getApplicationId());
        BeanUtils.copyProperties(commandRequest, agentHeartbeatBO);

        // 获取节点当前状态
        AgentReportStatusEnum statusEnum = getAgentReportStatus(agentHeartbeatBO);
        agentHeartbeatBO.setCurStatus(statusEnum);

        return agentHeartbeatBO;
    }

    /**
     * 获取企业版命令处理器
     *
     * @return map
     */
    private Map<Long, IAgentCommandProcessor> getEnterpriseCommandProcessorMap() {
        Map<Long, IAgentCommandProcessor> enterpriseCommandProcessorMap = new HashMap<>(commandProcessorMap);
        List<Object> enterpriseProcessorList = WebPluginUtils.getAgentCommandProcessor();
        if (!CollectionUtils.isEmpty(enterpriseProcessorList)) {
            enterpriseProcessorList.forEach(processor -> {
                if (processor instanceof IAgentCommandProcessor) {
                    enterpriseCommandProcessorMap.put(
                        ((IAgentCommandProcessor)processor).getCommand().getCommand(),
                        (IAgentCommandProcessor)processor);
                }
            });
        }
        return enterpriseCommandProcessorMap;
    }
}
