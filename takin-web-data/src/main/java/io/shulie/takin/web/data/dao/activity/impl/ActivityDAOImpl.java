package io.shulie.takin.web.data.dao.activity.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.mapper.mysql.ActivityNodeStateTableMapper;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.mapper.mysql.LinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.model.mysql.LinkManageTableEntity;
import io.shulie.takin.web.data.param.activity.ActivityCreateParam;
import io.shulie.takin.web.data.param.activity.ActivityExistsQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityUpdateParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.activity.ActivityResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
@Component
@Slf4j
public class ActivityDAOImpl implements ActivityDAO {

    @Resource
    private LinkManageTableMapper linkManageTableMapper;

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Resource
    private ActivityNodeStateTableMapper activityNodeStateTableMapper;

    @Override
    public List<Long> exists(ActivityExistsQueryParam param) {
        LambdaQueryWrapper<BusinessLinkManageTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            BusinessLinkManageTableEntity::getLinkId
        );
        if (param.getActivityName() != null) {
            wrapper.eq(BusinessLinkManageTableEntity::getLinkName, param.getActivityName());
        }
        if (param.getServiceName() != null) {
            wrapper.eq(BusinessLinkManageTableEntity::getEntrace,
                ActivityUtil.buildEntrance(param.getApplicationName(), param.getMethod(), param.getServiceName(),
                    param.getRpcType()));
        }
        if (StringUtils.isNotBlank(param.getVirtualEntrance())) {
            wrapper.eq(BusinessLinkManageTableEntity::getEntrace,
                ActivityUtil.buildVirtualEntrance(param.getVirtualEntrance(), param.getRpcType()));
        }

        wrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);
        List<BusinessLinkManageTableEntity> businessLinkManageTableEntities = businessLinkManageTableMapper.selectList(
            wrapper);
        if (CollectionUtils.isEmpty(businessLinkManageTableEntities)) {
            return Lists.newArrayList();
        }
        return businessLinkManageTableEntities.stream()
            .map(BusinessLinkManageTableEntity::getLinkId)
            .collect(Collectors.toList());
    }

    @Override
    public int createActivity(ActivityCreateParam param) {
        // 兼容老版本,先创建技术链路
        LinkManageTableEntity linkManageTableEntity = new LinkManageTableEntity();
        linkManageTableEntity.setLinkName(param.getActivityName());
        linkManageTableEntity.setEntrace(param.getEntrance());
        linkManageTableEntity.setChangeBefore(param.getChangeBefore());
        linkManageTableEntity.setChangeAfter(param.getChangeAfter());
        linkManageTableEntity.setIsChange(param.getIsChange() ? 1 : 0);
        linkManageTableEntity.setCustomerId(param.getCustomerId());
        linkManageTableEntity.setUserId(param.getUserId());
        linkManageTableEntity.setIsChange(0);
        linkManageTableEntity.setApplicationName(param.getApplicationName());
        Map<String, String> map = new HashMap<>();
        map.put(FeaturesConstants.EXTEND_KEY, param.getExtend());
        map.put(FeaturesConstants.METHOD_KEY, param.getMethod());
        map.put(FeaturesConstants.RPC_TYPE_KEY, param.getRpcType());
        map.put(FeaturesConstants.SERVICE_NAME_KEY, param.getServiceName());
        map.put(FeaturesConstants.SERVER_MIDDLEWARE_TYPE_KEY, param.getType().getType());
        linkManageTableEntity.setFeatures(JSON.toJSONString(map));

        // 再创建业务链路
        int insert1 = linkManageTableMapper.insert(linkManageTableEntity);
        param.setEntrance(param.getEntrance());
        param.setLinkId(linkManageTableEntity.getLinkId());
        return insert1 & createActivityNew(param);
    }

    @Override
    public int createActivityNew(ActivityCreateParam param) {
        BusinessLinkManageTableEntity businessLinkManageTableEntity = new BusinessLinkManageTableEntity();
        businessLinkManageTableEntity.setLinkName(param.getActivityName());
        businessLinkManageTableEntity.setEntrace(param.getEntrance());
        if (param.getBusinessType().equals(BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
            // 技术链路 只有正常业务活动才有
            businessLinkManageTableEntity.setRelatedTechLink(String.valueOf(param.getLinkId()));
        } else {
            // 数据兼容
            businessLinkManageTableEntity.setRelatedTechLink("0");
        }
        businessLinkManageTableEntity.setLinkLevel(param.getActivityLevel());
        businessLinkManageTableEntity.setIsChange(0);
        businessLinkManageTableEntity.setIsCore(param.getIsCore());
        businessLinkManageTableEntity.setBusinessDomain(param.getBusinessDomain());
        businessLinkManageTableEntity.setIsDeleted(0);
        businessLinkManageTableEntity.setCustomerId(param.getCustomerId());
        businessLinkManageTableEntity.setUserId(param.getUserId());
        businessLinkManageTableEntity.setCanDelete(0);
        if (null != param.getServerMiddlewareType()) {
            businessLinkManageTableEntity.setServerMiddlewareType(param.getServerMiddlewareType().getType());
        }
        // 业务活动类型
        businessLinkManageTableEntity.setType(param.getBusinessType());
        if (param.getBindBusinessId() != null) {
            // 虚拟业务活动
            businessLinkManageTableEntity.setBindBusinessId(param.getBindBusinessId());
        }
        return businessLinkManageTableMapper.insert(businessLinkManageTableEntity);
    }

    @Override
    public ActivityResult getActivityById(Long activityId) {
        BusinessLinkManageTableEntity businessLinkManageTableEntity =
            businessLinkManageTableMapper.selectById(activityId);
        if (businessLinkManageTableEntity == null || businessLinkManageTableEntity.getIsDeleted() == 1) {
            log.error("查询{}对应的业务活动失败，对应链路为空", activityId);
            return null;
        }

        ActivityResult result = new ActivityResult();

        // 业务活动
        if (ActivityUtil.isNormalBusiness(businessLinkManageTableEntity.getType())) {
            // 正常业务活动

            // 查询 业务链路 绑定的 技术链路
            String relatedTechLink = businessLinkManageTableEntity.getRelatedTechLink();
            LinkManageTableEntity linkManageTableEntity = linkManageTableMapper.selectById(relatedTechLink);
            if (linkManageTableEntity == null || linkManageTableEntity.getIsDeleted() == 1) {
                log.error("查询{}对应的业务活动失败，对应链路为空", activityId);
                return null;
            }

            if (StringUtils.isBlank(linkManageTableEntity.getFeatures())) {
                throw new RuntimeException("历史的业务数据无法使用，请重新配置或者数据订正");
            }

            // 技术链路
            result.setLinkId(Long.parseLong(relatedTechLink));
            EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(
                businessLinkManageTableEntity.getEntrace());
            result.setApplicationName(entranceJoinEntity.getApplicationName());
            // entranceName
            result.setEntranceName(entranceJoinEntity.getServiceName());
            Map<String, String> features = new HashMap<>(16);
            if (StringUtils.isNotBlank(linkManageTableEntity.getFeatures())) {
                features = JsonUtil.json2bean(linkManageTableEntity.getFeatures(), Map.class);
            }

            result.setExtend(features.get(FeaturesConstants.EXTEND_KEY));
            result.setMethod(features.get(FeaturesConstants.METHOD_KEY));
            result.setRpcType(features.get(FeaturesConstants.RPC_TYPE_KEY));
            result.setServiceName(features.get(FeaturesConstants.SERVICE_NAME_KEY));
            result.setType(EntranceTypeEnum.getEnumByType(features.get(FeaturesConstants.SERVER_MIDDLEWARE_TYPE_KEY)));

        } else if (businessLinkManageTableEntity.getType().equals(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
            // 虚拟业务活动
            EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertVirtualEntrance(
                businessLinkManageTableEntity.getEntrace());
            result.setVirtualEntrance(entranceJoinEntity.getVirtualEntrance());
            result.setRpcType(entranceJoinEntity.getRpcType());
            // 补充type
            result.setType(EntranceTypeEnum.getEnumByType(businessLinkManageTableEntity.getServerMiddlewareType()));
        }

        result.setBusinessType(businessLinkManageTableEntity.getType() != null
            ? businessLinkManageTableEntity.getType()
            : BusinessTypeEnum.NORMAL_BUSINESS.getType());
        result.setActivityId(businessLinkManageTableEntity.getLinkId());
        result.setActivityName(businessLinkManageTableEntity.getLinkName());
        result.setIsChange(businessLinkManageTableEntity.getIsChange() == 1);
        result.setUserId(businessLinkManageTableEntity.getUserId());
        result.setCustomerId(businessLinkManageTableEntity.getCustomerId());
        result.setActivityLevel(businessLinkManageTableEntity.getLinkLevel());
        result.setIsCore(businessLinkManageTableEntity.getIsCore());
        result.setBusinessDomain(businessLinkManageTableEntity.getBusinessDomain());
        return result;
    }

    @Override
    public int updateActivity(ActivityUpdateParam updateParam) {
        LinkManageTableEntity linkManageTableEntity = linkManageTableMapper.selectById(updateParam.getLinkId());
        if (StringUtils.isNotBlank(updateParam.getApplicationName())) {
            linkManageTableEntity.setApplicationName(updateParam.getApplicationName());
        }
        if (updateParam.getActivityName() != null) {
            linkManageTableEntity.setLinkName(updateParam.getActivityName());
        }

        if (updateParam.getEntranceName() != null) {
            linkManageTableEntity.setEntrace(updateParam.getEntrance());
            Map<String, String> features = JSON.parseObject(linkManageTableEntity.getFeatures(), Map.class);
            if (features == null) {
                features = Maps.newHashMap();
            }
            if (StringUtils.isNotBlank(updateParam.getExtend())) {
                features.put(FeaturesConstants.EXTEND_KEY, updateParam.getExtend());
            }
            if (StringUtils.isNotBlank(updateParam.getMethod())) {
                features.put(FeaturesConstants.METHOD_KEY, updateParam.getMethod());
            }
            if (StringUtils.isNotBlank(updateParam.getRpcType())) {
                features.put(FeaturesConstants.RPC_TYPE_KEY, updateParam.getRpcType());
            }
            if (StringUtils.isNotBlank(updateParam.getServiceName())) {
                features.put(FeaturesConstants.SERVICE_NAME_KEY, updateParam.getServiceName());
            }
            if (updateParam.getType() != null) {
                features.put(FeaturesConstants.SERVER_MIDDLEWARE_TYPE_KEY, updateParam.getType().getType());
            }
            linkManageTableEntity.setFeatures(JSON.toJSONString(features));
        }
        return updateActivityNew(updateParam) & linkManageTableMapper.updateById(linkManageTableEntity);
    }

    @Override
    public int updateActivityNew(ActivityUpdateParam updateParam) {
        BusinessLinkManageTableEntity businessLinkManageTableEntity = businessLinkManageTableMapper.selectById(
            updateParam.getActivityId());
        if (StringUtils.isNotBlank(updateParam.getActivityLevel())) {
            businessLinkManageTableEntity.setLinkLevel(updateParam.getActivityLevel());
        }
        if (StringUtils.isNotBlank(updateParam.getBusinessDomain())) {
            businessLinkManageTableEntity.setBusinessDomain(updateParam.getBusinessDomain());
        }
        if (updateParam.getIsCore() != null) {
            businessLinkManageTableEntity.setIsCore(updateParam.getIsCore());
        }
        if (updateParam.getEntrance() != null) {
            businessLinkManageTableEntity.setEntrace(updateParam.getEntrance());
        }
        if (updateParam.getActivityName() != null) {
            businessLinkManageTableEntity.setLinkName(updateParam.getActivityName());
        }
        EntranceTypeEnum middlewareType = updateParam.getServerMiddlewareType();
        if (middlewareType != null) {
            businessLinkManageTableEntity.setServerMiddlewareType(middlewareType.getType());
        }
        return businessLinkManageTableMapper.updateById(businessLinkManageTableEntity);
    }

    @Override
    public void deleteActivity(Long activityId) {
        BusinessLinkManageTableEntity businessLinkManageTableEntity = businessLinkManageTableMapper.selectById(
            activityId);
        BusinessLinkManageTableEntity updateBizLink = new BusinessLinkManageTableEntity();
        updateBizLink.setLinkId(businessLinkManageTableEntity.getLinkId());
        updateBizLink.setIsDeleted(1);
        businessLinkManageTableMapper.updateById(updateBizLink);

        if (ActivityUtil.isNormalBusiness(businessLinkManageTableEntity.getType())) {
            String relatedTechLink = businessLinkManageTableEntity.getRelatedTechLink();
            LinkManageTableEntity linkManageTableEntity = linkManageTableMapper.selectById(relatedTechLink);
            LinkManageTableEntity updateLink = new LinkManageTableEntity();
            updateLink.setIsDeleted(1);
            updateLink.setLinkId(linkManageTableEntity.getLinkId());
            linkManageTableMapper.updateById(linkManageTableEntity);
        }

    }

    @Override
    public PagingList<ActivityListResult> pageActivities(ActivityQueryParam param) {
        Page<BusinessLinkManageTableEntity> page = new Page<>();
        page.setCurrent(param.getCurrent() + 1);
        page.setSize(param.getPageSize());
        page.setOrders(Lists.newArrayList(OrderItem.desc("CREATE_TIME")));

        LambdaQueryWrapper<BusinessLinkManageTableEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getActivityName())) {
            lambdaQueryWrapper.like(BusinessLinkManageTableEntity::getLinkName, param.getActivityName());
        }
        if (StringUtils.isNotBlank(param.getDomain())) {
            lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getBusinessDomain, param.getDomain());
        }
        if (param.getIsChange() != null) {
            lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getIsChange, param.getIsChange());
        }
        if (CollectionUtils.isNotEmpty(param.getUserIdList())) {
            lambdaQueryWrapper.in(BusinessLinkManageTableEntity::getUserId, param.getUserIdList());
        }
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);

        Page<BusinessLinkManageTableEntity> tableEntityPage = businessLinkManageTableMapper
            .selectPage(page, lambdaQueryWrapper);

        if (CollectionUtils.isEmpty(tableEntityPage.getRecords())) {
            return PagingList.of(Lists.newArrayList(),tableEntityPage.getTotal());
        }

        List<String> techLinkIds = tableEntityPage.getRecords().stream().map(
            BusinessLinkManageTableEntity::getRelatedTechLink).collect(
            Collectors.toList());
        if (CollectionUtils.isEmpty(techLinkIds)) {
            return PagingList.empty();
        }

        List<Long> userIds = tableEntityPage.getRecords().stream().filter(f -> Objects.nonNull(f.getUserId())).map(
            BusinessLinkManageTableEntity::getUserId)
            .collect(Collectors.toList());
        List<LinkManageTableEntity> linkManageTableEntities = linkManageTableMapper.selectBatchIds(techLinkIds);
        Map<Long, LinkManageTableEntity> linkMap = linkManageTableEntities.stream()
            .collect(Collectors.toMap(LinkManageTableEntity::getLinkId, e -> e));
        Map<Long, UserExt> userExtMap = WebPluginUtils.getUserMapByIds(userIds);
        List<ActivityListResult> collect = tableEntityPage.getRecords().stream()
            .map(entity -> {
                ActivityListResult result = new ActivityListResult();
                result.setActivityId(entity.getLinkId());
                result.setActivityName(entity.getLinkName());
                result.setIsChange(entity.getIsChange());
                result.setIsCore(entity.getIsCore());
                result.setIsDeleted(entity.getIsDeleted());
                result.setUserId(entity.getUserId());
                result.setUserName(WebPluginUtils.getUserName(entity.getUserId(), userExtMap));
                result.setCreateTime(entity.getCreateTime());
                result.setUpdateTime(entity.getUpdateTime());
                result.setBusinessDomain(entity.getBusinessDomain());
                result.setCanDelete(entity.getCanDelete());
                if (entity.getRelatedTechLink() != null) {
                    LinkManageTableEntity linkManageTableEntity = linkMap.get(
                        Long.parseLong(entity.getRelatedTechLink()));
                    if (linkManageTableEntity != null) {

                    }
                }
                result.setActivityLevel(entity.getLinkLevel());
                result.setIsCore(entity.getIsCore());
                result.setBusinessDomain(entity.getBusinessDomain());
                result.setBusinessType(
                    entity.getType() != null ? entity.getType() : BusinessTypeEnum.NORMAL_BUSINESS.getType());
                return result;
            }).collect(Collectors.toList());
        return PagingList.of(collect, tableEntityPage.getTotal());
    }

    @Override
    public List<ActivityListResult> getActivityList(ActivityQueryParam param) {
        LambdaQueryWrapper<BusinessLinkManageTableEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (param.getBusinessType() != null) {
            lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getType, param.getBusinessType());
        }
        if (CollectionUtils.isNotEmpty(param.getActivityIds())) {
            lambdaQueryWrapper.in(BusinessLinkManageTableEntity::getLinkId, param.getActivityIds());
        }
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);
        List<BusinessLinkManageTableEntity> tableEntities = businessLinkManageTableMapper.selectList(
            lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(tableEntities)) {
            return Lists.newArrayList();
        }
        List<ActivityListResult> results = tableEntities.stream().map(entity -> {
            ActivityListResult result = new ActivityListResult();
            result.setActivityId(entity.getLinkId());
            result.setActivityName(entity.getLinkName());
            result.setIsChange(entity.getIsChange());
            result.setIsCore(entity.getIsCore());
            result.setIsDeleted(entity.getIsDeleted());
            result.setUserId(entity.getUserId());
            result.setCreateTime(entity.getCreateTime());
            result.setUpdateTime(entity.getUpdateTime());
            result.setBusinessDomain(entity.getBusinessDomain());
            result.setCanDelete(entity.getCanDelete());
            result.setActivityLevel(entity.getLinkLevel());
            result.setIsCore(entity.getIsCore());
            result.setBusinessDomain(entity.getBusinessDomain());
            result.setBindBusinessId(entity.getBindBusinessId());
            result.setBusinessType(
                entity.getType() != null ? entity.getType() : BusinessTypeEnum.NORMAL_BUSINESS.getType());
            return result;
        }).collect(Collectors.toList());
        return results;
    }

    @Override
    public void setActivityNodeServiceState(long activityId, String ownerApps, String serviceName, boolean state) {
        ActivityNodeState activityNodeState = new ActivityNodeState();
        String key;
        try {
            key = MD5Tool.getMD5(activityId + ownerApps + serviceName);
        } catch (Exception e) {
            return;
        }
        activityNodeState.setId(key);
        activityNodeState.setActivityId(activityId);
        activityNodeState.setOwnerApp(ownerApps);
        activityNodeState.setServiceName(serviceName);
        activityNodeState.setState(state);

        //SY:如果限制节点下只允许一个服务为打开状态则先清空再新增
//        activityNodeStateTableMapper.removeActivityNodeByActivityIdAndOwnerApp(activityNodeState);

        //SY:保存节点状态-如果存在则更新状态字段
        activityNodeStateTableMapper.setActivityNodeState(activityNodeState);
    }

    @Override
    public List<ActivityNodeState> getActivityNodeServiceState(long activityId) {
        return activityNodeStateTableMapper.getActivityNodes(activityId);
    }

}
