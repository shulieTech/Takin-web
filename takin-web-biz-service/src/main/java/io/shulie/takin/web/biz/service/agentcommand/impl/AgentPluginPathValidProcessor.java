package io.shulie.takin.web.biz.service.agentcommand.impl;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description agent插件存储路径校验器
 * @Author 南风
 * @Date 2021/11/11 3:24 下午
 */
@Service
public class AgentPluginPathValidProcessor extends AgentCommandSupport {

    private static final String VALID_STATUS_FIELD = "valid";

    private static final String ID_FIELD = "id";

    @Autowired
    private ApplicationPluginDownloadPathDAO pathDAO;

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.REPORT_AGENT_UPLOAD_PATH_STATUS;
    }

    @Override
    public void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject extras) {
        pathDAO.saveValidState(extras.getBoolean(VALID_STATUS_FIELD), extras.getLongValue(ID_FIELD));
    }

    @Override
    public boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        ApplicationPluginDownloadPathDetailResult result = getPluginDownloadPath(
            ApplicationAgentPathValidStatusEnum.TO_BE_CHECKED);
        return result != null;
    }

    @Override
    public Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO) {
        return new PluginDownloadPathResult(getPluginDownloadPath(ApplicationAgentPathValidStatusEnum.TO_BE_CHECKED));
    }

    @Override
    public AgentReportStatusEnum workStatus() {
        return AgentReportStatusEnum.RUNNING;
    }
}
