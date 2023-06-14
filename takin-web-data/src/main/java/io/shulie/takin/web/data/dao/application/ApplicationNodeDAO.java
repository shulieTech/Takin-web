package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.data.param.application.ApplicationNodeQueryParam;
import io.shulie.takin.web.data.param.application.QueryApplicationNodeParam;
import io.shulie.takin.web.data.result.application.ApplicationNodeListResult;
import io.shulie.takin.web.data.result.application.ApplicationNodeResult;

/**
 * 应用实例节点
 *
 * @author mubai
 * @date 2020-09-23 19:00
 */
public interface ApplicationNodeDAO {

    PagingList<ApplicationNodeResult> pageNodes(ApplicationNodeQueryParam param);

    /**
     * 应用节点列表分页
     *
     * @param param 请求参数
     * @return 应用节点列表分页
     */
    PagingList<ApplicationNodeListResult> pageNode(QueryApplicationNodeParam param);

    ApplicationNodeResult getNodeByAgentId(String agentId);

    /**
     * 获取在线应用的agentid
     * @param param
     * @return
     */
    List<String> getOnlineAgentIds(ApplicationNodeQueryParam param);

    Map<String,List<ApplicationNodeDTO>> getOnlineAgentIdsMap(ApplicationNodeQueryParam param);
}
