package io.shulie.takin.web.biz.service.application;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeDashBoardQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeResponse;

/**
 * @author mubai
 * @date 2020-09-23 19:22
 */
public interface ApplicationNodeService {

    PagingList<ApplicationNodeResponse> pageNodes(ApplicationNodeQueryRequest request);

    /**
     * 节点列表分页, v2 版本
     *
     * @param request 需要入参
     * @return 节点列表
     */
    PagingList<ApplicationNodeResponse> pageNodesV2(ApplicationNodeQueryRequest request);

    ApplicationNodeResponse getNodeByAgentId(String agentId);

    /**
     * 删除zk节点
     */
    void deleteZkNode(String appName, String agentId);

    /**
     * 应用节点信息
     *
     * @param applicationId 应用id
     * @return 应用节点信息
     */
    ApplicationNodeDashBoardResponse getApplicationNodeInfo(Long applicationId);

    /**
     * 探针的操作
     * 安装, 升级, 卸载
     *
     * @param request 操作所需要的参数
     */
    void operateProbe(ApplicationNodeOperateProbeRequest request);

    ApplicationNodeDashBoardResponse getApplicationNodeAmount(ApplicationNodeDashBoardQueryRequest request);

    /**
     * 根据应用名称, 获得节点探针相应数据
     *
     * @param applicationName 应用名称
     * @param nodeNum 应用配置的节点个数
     * @return 探针数据
     */
    ApplicationNodeDashBoardResponse getApplicationNodeDashBoardResponse(String applicationName, Integer nodeNum);

}
