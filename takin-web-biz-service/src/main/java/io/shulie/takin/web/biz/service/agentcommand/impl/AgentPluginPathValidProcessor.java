package io.shulie.takin.web.biz.service.agentcommand.impl;

import java.util.Objects;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
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

    private static final String VALID_STATUS_FIELD = "validStatus";

    @Autowired
    private ApplicationPluginDownloadPathDAO pathDAO;

    @Override
    public void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject commandParam) {
        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryDetailByCustomerId();
        if (Objects.isNull(result)) {
            return;
        }
        pathDAO.saveValidState(commandParam.getBoolean(VALID_STATUS_FIELD), result.getId());
    }

    @Override
    public boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        //查库

        // TODO ocean_wll
        return false;
    }

    @Override
    public Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO) {
        // TODO ocean_wll
        return null;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.REPORT_AGENT_UPLOAD_PATH_STATUS;
    }
}
