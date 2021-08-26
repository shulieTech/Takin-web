package io.shulie.takin.web.biz.service.agent;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.application.AgentPluginSupportResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.MiddleWareResponse;

/**
 * agent 中插件支持的功能
 *
 * @author shiyajian
 * create: 2020-10-09
 */
public interface AgentPluginSupportService {

    /**
     * 获得agent插件的支持列表
     */
    List<AgentPluginSupportResponse> queryAgentPluginSupportList();

    // 2、新增 agent 插件和jar包的关联关系

    // 3、删除 agent 插件和 jar包的关联关系

    /**
     * 判断 agent 是否支持当前jar包
     */
    Boolean isSupportLib(List<AgentPluginSupportResponse> supportList, String libName);

    MiddleWareResponse convertLibInfo(List<AgentPluginSupportResponse> supportList, String libName);
}
