package io.shulie.takin.web.biz.service.agentcommand.impl;

import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
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

//    @Override
//    public Object process(AgentCommandBO commandParam) {
//        JSONObject obj = JSONObject.parseObject(commandParam.getExtras());
//        long recordId = obj.getLongValue(ID_FIELD);
//        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryById(recordId);
//        if (Objects.isNull(result) || StringUtils.isBlank(commandParam.getExtras())) {
//            throw new TakinWebException(TakinWebExceptionEnum.AGENT_COMMAND_VALID_ERROR,
//                    "agent command operate error. commandId:" + commandParam.getId());
//        }
//        pathDAO.saveValidState(obj.getBoolean(VALID_STATUS_FIELD),result.getId());
//        return null;
//    }
//
    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.REPORT_AGENT_UPLOAD_PATH_STATUS;
    }

    @Override
    public void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject extras) {

    }

    @Override
    public boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        return false;
    }

    @Override
    public Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO) {
        return null;
    }
}
