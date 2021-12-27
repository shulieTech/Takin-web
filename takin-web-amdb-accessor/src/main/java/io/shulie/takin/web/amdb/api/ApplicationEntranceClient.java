package io.shulie.takin.web.amdb.api;

import java.util.List;

import cn.hutool.json.JSONObject;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.takin.web.amdb.bean.query.application.BatchNodeMetricsQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.QueryMetricsFromAMDB;
import io.shulie.takin.web.amdb.bean.query.application.TempTopologyQuery1;
import io.shulie.takin.web.amdb.bean.query.application.TempTopologyQuery2;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
public interface ApplicationEntranceClient {

    List<ServiceInfoDTO> getApplicationEntrances(String applicationName, String entranceType);

    LinkTopologyDTO getApplicationEntrancesTopology(boolean tempActivity, String applicationName, String linkId, String serviceName,
                                                    String method, String rpcType, String extend);

    String queryMetricsFromAMDB1(TempTopologyQuery1 tempTopologyQuery1);
    JSONObject queryMetricsFromAMDB2(TempTopologyQuery2 tempTopologyQuery2);
    JSONObject queryMetrics(QueryMetricsFromAMDB queryMetricsFromAMDB);
    List<JSONObject> queryBatchMetrics(BatchNodeMetricsQueryDTO batchNodeMetricsQueryDTO);

    Boolean updateUnknownNodeToOuter(String applicationName, String linkId, String serviceName, String method,
        String rpcType, String extend, String nodeId);

    List<ServiceInfoDTO> getMqTopicGroups(String applicationName);
}
