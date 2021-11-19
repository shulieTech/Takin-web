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
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandBO;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentHeartbeatRequest;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.agentcommand.IAgentCommandProcessor;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentHeartbeatService;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentUpgradeEnum;
import io.shulie.takin.web.common.enums.excel.BooleanEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.ProbeStatusEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
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

    /**
     * 企业版标识
     */
    private final static String ENTERPRISE_FLAG = "shulieEnterprise";

    @Resource
    private AgentReportService agentReportService;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private ApplicationPluginUpgradeService applicationPluginUpgradeService;

    @Resource
    private ThreadPoolExecutor agentHeartbeatThreadPool;

    private final Map<Long, IAgentCommandProcessor> commandProcessorMap = new HashMap<>();

    public AgentHeartbeatServiceImpl(List<IAgentCommandProcessor> processorList) {
        processorList.forEach(processor -> commandProcessorMap.put(processor.getCommand().getCommand(), processor));
    }

    public List<AgentCommandBO> process(AgentHeartbeatRequest commandRequest) {

        // TODO ocean_wll 加个判断是否企业版，根据企业版的不同，执行不同的对象
        Boolean isEnterprise = ENTERPRISE_FLAG.equals(commandRequest.getFlag());

        // 检测状态
        AgentHeartbeatBO agentHeartbeatBO = buildAgentHeartBeatBO(commandRequest);
        AgentReportStatusEnum statusEnum = getAgentReportStatus(agentHeartbeatBO);

        // 异步处理上报的命令数据
        if (!CollectionUtils.isEmpty(commandRequest.getCommandResult())) {
            commandRequest.getCommandResult().forEach(commandResult ->
                agentHeartbeatThreadPool.execute(() -> {
                    IAgentCommandProcessor processor = commandProcessorMap.get(commandResult.getId());
                    if (processor != null) {
                        processor.process(agentHeartbeatBO, commandResult);
                    }
                })
            );
        }

        CreateAgentReportParam createAgentReportParam = new CreateAgentReportParam();
        BeanUtils.copyProperties(agentHeartbeatBO, createAgentReportParam);
        createAgentReportParam.setApplicationName(agentHeartbeatBO.getProjectName());
        createAgentReportParam.setStatus(statusEnum.getVal());

        // 数据入库
        agentReportService.insertOrUpdate(createAgentReportParam);

        List<Future<AgentCommandBO>> futures = new ArrayList<>();

        // 异步处理各种命令
        for (Map.Entry<Long, IAgentCommandProcessor> entry : commandProcessorMap.entrySet()) {
            Future<AgentCommandBO> future = agentHeartbeatThreadPool.submit(
                () -> entry.getValue().dealHeartbeat(agentHeartbeatBO));
            futures.add(future);
        }
        List<AgentCommandBO> commandBOList = new ArrayList<>();
        futures.forEach(future -> {
            try {
                AgentCommandBO agentCommandBO = future.get(60, TimeUnit.SECONDS);
                if (agentCommandBO != null) {
                    commandBOList.add(agentCommandBO);
                }
            } catch (Exception e) {
                // ignore
            }
        });

        return filterCommand(commandBOList);
    }

    /**
     * 过滤指令，有些指令不能同时返回
     *
     * @param commandBOList 指令集合
     * @return AgentCommandBO集合
     */
    private List<AgentCommandBO> filterCommand(List<AgentCommandBO> commandBOList) {

        // TODO ocean_wll 过滤指令
        return commandBOList;
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
        AgentHeartbeatBO agentHeartBeatBO = new AgentHeartbeatBO();
        agentHeartBeatBO.setApplicationId(applicationMnt.getApplicationId());
        BeanUtils.copyProperties(commandRequest, agentHeartBeatBO);
        return agentHeartBeatBO;
    }
}
