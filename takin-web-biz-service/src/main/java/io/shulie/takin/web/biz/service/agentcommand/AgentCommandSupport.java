package io.shulie.takin.web.biz.service.agentcommand;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandBO;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description agent指令
 * @Author ocean_wll
 * @Date 2021/11/11 2:45 下午
 */
public abstract class AgentCommandSupport implements IAgentCommandProcessor {

    @Resource
    protected ApplicationPluginDownloadPathDAO applicationPluginDownloadPathDAO;

    /**
     * agent默认的升级批次号
     */
    protected final static String DEFAULT_UPGRADE_BATH = "-1";

    /**
     * 处理业务数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @param extras           指令参数
     */
    public abstract void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject extras);

    /**
     * 判断是否需要处理心跳数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @return true:需要处理，false:不需要处理
     */
    public abstract boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO);

    /**
     * 处理心跳数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @return 不同指令的数据
     */
    public abstract Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO);

    @Override
    public void process(AgentHeartbeatBO agentHeartbeatBO, AgentCommandBO commandParam) {
        // 因为这个方法是在线程池中异步调用，所以不允许抛出异常
        try {
            if (StringUtils.isBlank(commandParam.getExtras())) {
                return;
            }
            process0(agentHeartbeatBO, JSON.parseObject(commandParam.getExtras()));
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public AgentCommandBO dealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        // 当前节点不是运行中，并且心跳数据当前指令不需要处理则返回null
        if (!AgentReportStatusEnum.RUNNING.equals(agentHeartbeatBO.getCurStatus())
            || !needDealHeartbeat(agentHeartbeatBO)) {
            return null;
        }

        AgentCommandBO commandBO;
        Object dealResult = dealHeartbeat0(agentHeartbeatBO);
        if (dealResult == null) {
            commandBO = new AgentCommandBO(getCommand().getCommand(), "");
        } else {
            commandBO = new AgentCommandBO(getCommand().getCommand(), JSON.toJSONString(dealResult));
        }

        return commandBO;
    }

    /**
     * 获取当前租户的插件上传信息
     *
     * @param statusEnum 状态枚举
     * @return ApplicationPluginDownloadPathDetailResult对象
     */
    protected ApplicationPluginDownloadPathDetailResult getPluginDownloadPath(
        ApplicationAgentPathValidStatusEnum statusEnum) {
        return applicationPluginDownloadPathDAO.queryDetailByTenant(statusEnum);
    }
}
