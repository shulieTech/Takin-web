package io.shulie.takin.web.data.dao.linkmanage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.excel.util.StringUtils;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.linkmanage.TSceneLinkRelateMapper;
import com.pamirs.takin.entity.domain.entity.linkmanage.SceneLinkRelate;
import io.shulie.takin.web.data.convert.linkmanage.BusinessLinkManageConvert;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.mapper.mysql.LinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.model.mysql.LinkManageTableEntity;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageCreateParam;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageQueryParam;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageUpdateParam;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.data.result.linkmange.TechLinkResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author fanxx
 * @date 2020/10/16 5:21 下午
 */
@Component
public class BusinessLinkManageDAOImpl implements BusinessLinkManageDAO, MPUtil<BusinessLinkManageTableEntity> {

    @Autowired
    private TSceneLinkRelateMapper tSceneLinkRelateMapper;

    @Autowired
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Autowired
    private LinkManageTableMapper linkManageTableMapper;

    @Override
    public BusinessLinkResult selectBussinessLinkById(Long id) {
        BusinessLinkResult businessLinkResult = new BusinessLinkResult();
        LambdaQueryWrapper<BusinessLinkManageTableEntity> businessLinkManageWrapper = new LambdaQueryWrapper<>();
        businessLinkManageWrapper.select(
            BusinessLinkManageTableEntity::getLinkId,
            BusinessLinkManageTableEntity::getLinkName,
            BusinessLinkManageTableEntity::getEntrace,
            BusinessLinkManageTableEntity::getCreateTime,
            BusinessLinkManageTableEntity::getUpdateTime,
            BusinessLinkManageTableEntity::getIsChange,
            BusinessLinkManageTableEntity::getCanDelete,
            BusinessLinkManageTableEntity::getIsCore,
            BusinessLinkManageTableEntity::getBusinessDomain,
            BusinessLinkManageTableEntity::getLinkLevel,
            BusinessLinkManageTableEntity::getRelatedTechLink,
            BusinessLinkManageTableEntity::getTenantId,
            BusinessLinkManageTableEntity::getEnvCode,
            BusinessLinkManageTableEntity::getUserId
        );
        businessLinkManageWrapper.eq(BusinessLinkManageTableEntity::getLinkId, id);
        businessLinkManageWrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);
        BusinessLinkManageTableEntity businessLinkManageTableEntity = businessLinkManageTableMapper.selectOne(businessLinkManageWrapper);
        if (Objects.isNull(businessLinkManageTableEntity)) {
            return businessLinkResult;
        } else {
            businessLinkResult.setId(String.valueOf(businessLinkManageTableEntity.getLinkId()));
            businessLinkResult.setLinkName(businessLinkManageTableEntity.getLinkName());
            businessLinkResult.setEntrace(businessLinkManageTableEntity.getEntrace());
            businessLinkResult.setIschange(String.valueOf(businessLinkManageTableEntity.getIsChange()));
            businessLinkResult.setCreateTime(businessLinkManageTableEntity.getCreateTime());
            businessLinkResult.setUpdateTime(businessLinkManageTableEntity.getUpdateTime());
            businessLinkResult.setCandelete(String.valueOf(businessLinkManageTableEntity.getCanDelete()));
            businessLinkResult.setIsCore(String.valueOf(businessLinkManageTableEntity.getIsCore()));
            businessLinkResult.setLinkLevel(businessLinkManageTableEntity.getLinkLevel());
            businessLinkResult.setBusinessDomain(businessLinkManageTableEntity.getBusinessDomain());
            businessLinkResult.setTenantId(businessLinkManageTableEntity.getTenantId());
            businessLinkResult.setEnvCode(businessLinkManageTableEntity.getEnvCode());
            businessLinkResult.setUserId(businessLinkManageTableEntity.getUserId());
        }

        LambdaQueryWrapper<LinkManageTableEntity> linkManageWrapper = new LambdaQueryWrapper<>();
        linkManageWrapper.select(
            LinkManageTableEntity::getLinkId,
            LinkManageTableEntity::getLinkName,
            LinkManageTableEntity::getChangeBefore,
            LinkManageTableEntity::getChangeAfter,
            LinkManageTableEntity::getChangeRemark,
            LinkManageTableEntity::getChangeType
        );
        linkManageWrapper.eq(LinkManageTableEntity::getLinkId, businessLinkManageTableEntity.getRelatedTechLink());
        LinkManageTableEntity linkManageTableEntity = linkManageTableMapper.selectOne(linkManageWrapper);
        TechLinkResult techLinkResult = new TechLinkResult();
        businessLinkResult.setTechLinkResult(techLinkResult);
        if (Objects.isNull(linkManageTableEntity)) {
            return businessLinkResult;
        } else {
            techLinkResult.setLinkId(linkManageTableEntity.getLinkId());
            techLinkResult.setTechLinkName(linkManageTableEntity.getLinkName());
            techLinkResult.setBodyBefore(linkManageTableEntity.getChangeBefore());
            techLinkResult.setBodyAfter(linkManageTableEntity.getChangeAfter());
            techLinkResult.setChangeRemark(linkManageTableEntity.getChangeRemark());
            techLinkResult.setChangeType(String.valueOf(linkManageTableEntity.getChangeType()));
        }
        return businessLinkResult;
    }

    @Override
    public List<BusinessLinkResult> selectBussinessLinkByIdList(List<Long> ids) {
        List<BusinessLinkManageTableEntity> entityList = businessLinkManageTableMapper.selectList(this.getLambdaQueryWrapper()
            .select(BusinessLinkManageTableEntity::getLinkId, BusinessLinkManageTableEntity::getLinkName,
                BusinessLinkManageTableEntity::getEntrace, BusinessLinkManageTableEntity::getServerMiddlewareType,
                BusinessLinkManageTableEntity::getType)
            .in(BusinessLinkManageTableEntity::getLinkId, ids)
            .eq(BusinessLinkManageTableEntity::getIsDeleted, 0));
        if (CollectionUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }

        return entityList.stream().map(businessLinkManageTableEntity -> {
            BusinessLinkResult businessLinkResult = new BusinessLinkResult();
            businessLinkResult.setId(String.valueOf(businessLinkManageTableEntity.getLinkId()));
            businessLinkResult.setLinkName(businessLinkManageTableEntity.getLinkName());
            businessLinkResult.setEntrace(businessLinkManageTableEntity.getEntrace());
            businessLinkResult.setType(businessLinkManageTableEntity.getType());
            businessLinkResult.setServerMiddlewareType(businessLinkManageTableEntity.getServerMiddlewareType());
            return businessLinkResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BusinessLinkResult> selectList(BusinessLinkManageQueryParam queryParam) {
        List<BusinessLinkResult> resultList = Lists.newArrayList();
        LambdaQueryWrapper<BusinessLinkManageTableEntity> businessLinkManageWrapper = new LambdaQueryWrapper<>();
        businessLinkManageWrapper.select(
                BusinessLinkManageTableEntity::getLinkId,
                BusinessLinkManageTableEntity::getLinkName
        );
        if (CollectionUtils.isNotEmpty(queryParam.getUserIdList())) {
            businessLinkManageWrapper.in(BusinessLinkManageTableEntity::getUserId, queryParam.getUserIdList());
        }
        if (!StringUtils.isEmpty(queryParam.getBussinessActiveName())) {
            businessLinkManageWrapper.like(BusinessLinkManageTableEntity::getLinkName, queryParam.getBussinessActiveName());
        }
        businessLinkManageWrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);
        List<BusinessLinkManageTableEntity> entityList = businessLinkManageTableMapper.selectList(businessLinkManageWrapper);
        if (CollectionUtils.isNotEmpty(entityList)) {
            resultList = entityList.stream().map(businessLinkManageTableEntity -> {
                BusinessLinkResult businessLinkResult = new BusinessLinkResult();
                businessLinkResult.setId(String.valueOf(businessLinkManageTableEntity.getLinkId()));
                businessLinkResult.setLinkName(businessLinkManageTableEntity.getLinkName());
                return businessLinkResult;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    @Override
    public int insert(BusinessLinkManageCreateParam param) {
        BusinessLinkManageTableEntity entity = new BusinessLinkManageTableEntity();
        BeanUtils.copyProperties(param, entity);
        return businessLinkManageTableMapper.insert(entity);
    }

    /**
     * 指定责任人-业务活动
     *
     * @param param
     * @return
     */
    @Override
    public int allocationUser(BusinessLinkManageUpdateParam param) {
        LambdaUpdateWrapper<BusinessLinkManageTableEntity> wrapper = new LambdaUpdateWrapper();
        wrapper.set(BusinessLinkManageTableEntity::getUserId, param.getUserId())
            .eq(BusinessLinkManageTableEntity::getLinkId, param.getLinkId());
        return businessLinkManageTableMapper.update(null, wrapper);
    }

    @Override
    public List<BusinessLinkResult> getList() {
        LambdaQueryWrapper<BusinessLinkManageTableEntity> wrapper = new LambdaQueryWrapper();
        wrapper.orderByDesc(BusinessLinkManageTableEntity::getUpdateTime);
        List<BusinessLinkManageTableEntity> entities = businessLinkManageTableMapper.selectList(wrapper);
        return BusinessLinkManageConvert.INSTANCE.ofList(entities);
    }

    @Override
    public List<BusinessLinkResult> getListByIds(List<Long> ids) {
        LambdaQueryWrapper<BusinessLinkManageTableEntity> wrapper = new LambdaQueryWrapper();
        wrapper.in(BusinessLinkManageTableEntity::getLinkId, ids);
        wrapper.orderByDesc(BusinessLinkManageTableEntity::getUpdateTime);
        List<BusinessLinkManageTableEntity> entities = businessLinkManageTableMapper.selectList(wrapper);
        return BusinessLinkManageConvert.INSTANCE.ofList(entities);
    }

    @Override
    public List<Long> listIdsByBusinessFlowId(Long businessFlowId) {
        List<SceneLinkRelate> sceneLinkRelates = tSceneLinkRelateMapper.selectBySceneId(businessFlowId);
        return CollectionUtils.isEmpty(sceneLinkRelates)
            ? Collections.emptyList()
            : sceneLinkRelates.stream()
                .map(sceneLinkRelate -> Long.valueOf(sceneLinkRelate.getBusinessLinkId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLinkManageTableEntity> listByIds(List<Long> businessActivityIds) {
         return businessLinkManageTableMapper.selectBatchIds(businessActivityIds);
    }

}
