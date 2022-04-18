package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    private ExecutorService agentHeartbeatThreadPool;

    /**
     * 非企业版处理器集合
     */
    private final Map<Long, IAgentCommandProcessor> commandProcessorMap = new HashMap<>();

    public AgentHeartbeatServiceImpl(List<IAgentCommandProcessor> processorList) {
        processorList.forEach(processor -> commandProcessorMap.put(processor.getCommand().getCommand(), processor));
    }

    @Override
    public List<AgentCommandResBO> process(AgentHeartbeatRequest commandRequest) {

        // 1、获取处理器集合
        Map<Long, IAgentCommandProcessor> processorMap = getCommandProcessorMap(commandRequest.getFlag());

        // 2、检测状态
        AgentHeartbeatBO agentHeartbeatBO = buildAgentHeartBeatBO(commandRequest);

        // 3、异步处理上报的数据
        // 需要跳过的指令
        List<Long> skipCommandIds = asyncProcessCommandResult(commandRequest, agentHeartbeatBO, processorMap);

        // 4、保存心跳数据
        saveHeartbeatData(agentHeartbeatBO);

        // 5、阻塞多线程处理心跳数据，最多等待60s；
        return asyncDealHeartbeatData(agentHeartbeatBO, processorMap, skipCommandIds);
    }

    /**
     * 获取处理器map
     *
     * @param flag 标识：是否为企业版
     * @return map
     */
    private Map<Long, IAgentCommandProcessor> getCommandProcessorMap(String flag) {
        // 判断是否为企业版
        boolean isEnterprise = ENTERPRISE_FLAG.equals(flag);

        if (!isEnterprise) {
            return commandProcessorMap;
        }

        Map<Long, IAgentCommandProcessor> enterpriseCommandProcessorMap = new HashMap<>(commandProcessorMap);
        List<Object> enterpriseProcessorList = WebPluginUtils.getAgentCommandProcessor();
        if (!CollectionUtils.isEmpty(enterpriseProcessorList)) {
            enterpriseProcessorList.forEach(processor -> {
                if (processor instanceof IAgentCommandProcessor) {
                    enterpriseCommandProcessorMap.put(
                            ((IAgentCommandProcessor) processor).getCommand().getCommand(),
                            (IAgentCommandProcessor) processor);
                }
            });
        }
        return enterpriseCommandProcessorMap;
    }

    /**
     * AgentCommandRequest -> AgentHeartBeatBO
     *
     * @param commandRequest AgentCommandRequest对象
     * @return AgentHeartBeatBO对象
     */
    private AgentHeartbeatBO buildAgentHeartBeatBO(AgentHeartbeatRequest commandRequest) {
        Long applicationId = applicationService.queryApplicationIdByAppName(commandRequest.getProjectName());
        if (applicationId == null) {
            throw new TakinWebException(ExceptionCode.AGENT_REGISTER_ERROR, "应用名不存在");
        }

        AgentHeartbeatBO agentHeartbeatBO = new AgentHeartbeatBO();
        agentHeartbeatBO.setApplicationId(applicationId);
        BeanUtils.copyProperties(commandRequest, agentHeartbeatBO);

        // 获取节点当前状态
        AgentReportStatusEnum statusEnum = getAgentReportStatus(agentHeartbeatBO);
        agentHeartbeatBO.setCurStatus(statusEnum);

        return agentHeartbeatBO;
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
     * 异步处理上报数据
     *
     * @param commandRequest   请求数据
     * @param agentHeartbeatBO 心跳数据
     * @param processorMap     处理器集合
     * @return 需要跳过的指令
     */
    private List<Long> asyncProcessCommandResult(AgentHeartbeatRequest commandRequest,
                                                 AgentHeartbeatBO agentHeartbeatBO, Map<Long, IAgentCommandProcessor> processorMap) {
        // 需要跳过的指令
        List<Long> skipCommandIds = new ArrayList<>();
        // 异步处理上报的命令数据
        if (!CollectionUtils.isEmpty(commandRequest.getCommandResult())) {
            // 记录日志
            reportLog.info("envCode:{}, tenantId:{}, agentHeartbeatBO:{}, commands:{}",
                    WebPluginUtils.traceEnvCode(),
                    WebPluginUtils.traceTenantId(),
                    agentHeartbeatBO,
                    commandRequest.getCommandResult());

            commandRequest.getCommandResult().forEach(commandResult ->
                    agentHeartbeatThreadPool.execute(() -> {
                        skipCommandIds.add(commandResult.getId());
                        IAgentCommandProcessor processor = processorMap.get(commandResult.getId());
                        if (processor != null) {
                            processor.process(agentHeartbeatBO, commandResult);
                        }
                    })
            );
        }
        return skipCommandIds;
    }

    /**
     * 心跳数据入库
     *
     * @param agentHeartbeatBO 心跳数据
     */
    private void saveHeartbeatData(AgentHeartbeatBO agentHeartbeatBO) {
        // 清空多余数据
        agentReportService.clearExpiredData();
        CreateAgentReportParam createAgentReportParam = new CreateAgentReportParam();
        BeanUtils.copyProperties(agentHeartbeatBO, createAgentReportParam);
        createAgentReportParam.setApplicationName(agentHeartbeatBO.getProjectName());
        createAgentReportParam.setStatus(agentHeartbeatBO.getCurStatus().getVal());
        agentReportService.insertOrUpdate(createAgentReportParam);
    }

    /**
     * 异步处理心跳数据
     *
     * @param agentHeartbeatBO 心跳数据
     * @param processorMap     处理器集合
     * @param skipCommandIds   跳过的指令
     * @return AgentCommandResBO集合
     */
    private List<AgentCommandResBO> asyncDealHeartbeatData(AgentHeartbeatBO agentHeartbeatBO,
                                                           Map<Long, IAgentCommandProcessor> processorMap, List<Long> skipCommandIds) {

        List<Future<AgentCommandResBO>> futures = new ArrayList<>();
        // 异步处理各种命令
        for (Map.Entry<Long, IAgentCommandProcessor> entry : processorMap.entrySet()) {
            if (skipCommandIds.contains(entry.getKey())) {
                continue;
            }
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

        // 记录日志
        if (!CollectionUtils.isEmpty(result)) {
            distributionLog.info("envCode:{}, tenantId:{}, agentHeartbeatBO:{}, commands:{}",
                    WebPluginUtils.traceEnvCode(), WebPluginUtils.traceTenantId(), agentHeartbeatBO, result);
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
}
