package io.shulie.takin.web.biz.service.scene.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.amdb.common.dto.link.topology.LinkNodeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.amdb.common.enums.NodeTypeEnum;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.mapper.mysql.LinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.model.mysql.LinkManageTableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/8/12 上午9:51
 */
@Service
@Slf4j
public class ApplicationBusinessActivityServiceImpl implements ApplicationBusinessActivityService {

    @Autowired
    private ApplicationEntranceClient applicationEntranceClient;

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Resource
    private LinkManageTableMapper linkManageTableMapper;

    @Override
    public List<String> processAppNameByBusinessActiveId(Long businessActivityId) {
        BusinessLinkManageTableEntity businessLinkManageTableEntity = businessLinkManageTableMapper.selectById(
                businessActivityId);
        if (businessLinkManageTableEntity == null) {
            return Lists.newArrayList();
        }
        // 虚拟入口 返回数据
        if (businessLinkManageTableEntity.getType() != null && businessLinkManageTableEntity.getType().equals(
                BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
            return Lists.newArrayList();
        }
        LinkManageTableEntity linkManageTableEntity = linkManageTableMapper.selectById(
                businessLinkManageTableEntity.getRelatedTechLink());
        if (linkManageTableEntity == null) {
            return Lists.newArrayList();
        }
        String features = linkManageTableEntity.getFeatures();
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(features)) {
            map = JSON.parseObject(features, Map.class);
        }
        String serviceName = map.get(FeaturesConstants.SERVICE_NAME_KEY);
        String methodName = map.get(FeaturesConstants.METHOD_KEY);
        String rpcType = map.get(FeaturesConstants.RPC_TYPE_KEY);
        String extend = map.get(FeaturesConstants.EXTEND_KEY);
        LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
                false, linkManageTableEntity.getApplicationName(),
                null, serviceName, methodName, rpcType, extend, false);
        if (applicationEntrancesTopology == null) {
            return Lists.newArrayList();
        }
        List<LinkNodeDTO> nodes = applicationEntrancesTopology.getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            return Lists.newArrayList();
        }
        return nodes.stream()
                .filter(node -> node.getNodeType().equals(NodeTypeEnum.APP.getType()))
                .map(LinkNodeDTO::getNodeName)
                .collect(Collectors.toList());
    }
}
