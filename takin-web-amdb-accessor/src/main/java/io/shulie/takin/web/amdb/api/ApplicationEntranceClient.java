package io.shulie.takin.web.amdb.api;

import java.util.List;

import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
public interface ApplicationEntranceClient {

    List<ServiceInfoDTO> getApplicationEntrances(String applicationName, String entranceType);

    LinkTopologyDTO getApplicationEntrancesTopology(String applicationName, String linkId, String serviceName,
        String method, String rpcType, String extend);

    Boolean updateUnknownNodeToOuter(String applicationName, String linkId, String serviceName, String method,
        String rpcType, String extend, String nodeId);

    List<ServiceInfoDTO> getMqTopicGroups(String applicationName);
}
