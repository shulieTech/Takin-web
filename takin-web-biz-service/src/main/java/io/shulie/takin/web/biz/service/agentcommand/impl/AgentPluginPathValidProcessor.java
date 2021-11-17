package io.shulie.takin.web.biz.service.agentcommand.impl;

import java.util.Objects;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandBO;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandDStateEnum;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import org.apache.commons.lang3.StringUtils;
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
    public Object process(AgentCommandBO commandParam) {
        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryDetailByCustomerId();
        if (Objects.isNull(result) || StringUtils.isBlank(commandParam.getExtras())) {
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_COMMAND_VALID_ERROR,
                "agent command operate error. commandId:" + commandParam.getId());
        }
        JSONObject obj = JSONObject.parseObject(commandParam.getExtras());
        pathDAO.saveValidState(AgentCommandDStateEnum.getState(obj.getString(VALID_STATUS_FIELD)), result.getId());
        return null;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.REPORT_AGENT_UPLOAD_PATH_STATUS;
    }
}
