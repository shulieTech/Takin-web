package io.shulie.takin.web.data.dao.linkmanage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.LinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.LinkManageTableEntity;
import io.shulie.takin.web.data.param.linkmanage.LinkManageCreateParam;
import io.shulie.takin.web.data.param.linkmanage.LinkManageQueryParam;
import io.shulie.takin.web.data.param.linkmanage.LinkManageUpdateParam;
import io.shulie.takin.web.data.result.linkmange.LinkManageResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统流程dao
 *
 * @author fanxx
 * @date 2020/10/19 7:49 下午
 */
@Component
public class LinkManageDAOImpl implements LinkManageDAO, MPUtil<LinkManageTableEntity> {

    @Autowired
    private LinkManageTableMapper linkManageTableMapper;

    @Override
    public LinkManageResult selectLinkManageById(Long id) {
        LambdaQueryWrapper<LinkManageTableEntity> linkManageWrapper = getLimitOneLambdaQueryWrapper()
            .eq(LinkManageTableEntity::getLinkId, id).select(
                LinkManageTableEntity::getLinkId,
                LinkManageTableEntity::getLinkName,
                LinkManageTableEntity::getApplicationName,
                LinkManageTableEntity::getTenantId,
                LinkManageTableEntity::getEnvCode,
                LinkManageTableEntity::getUserId,
                LinkManageTableEntity::getFeatures
            );

        LinkManageTableEntity linkManageTableEntity = linkManageTableMapper.selectOne(linkManageWrapper);

        LinkManageResult linkManageResult = new LinkManageResult();
        if (Objects.isNull(linkManageTableEntity)) {
            return linkManageResult;
        }

        BeanUtils.copyProperties(linkManageTableEntity, linkManageResult);
        return linkManageResult;
    }

    @Override
    public List<LinkManageResult> selectList(LinkManageQueryParam queryParam) {
        List<LinkManageResult> linkManageResultList = Lists.newArrayList();
        LambdaQueryWrapper<LinkManageTableEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(
            LinkManageTableEntity::getLinkId,
            LinkManageTableEntity::getLinkName,
            LinkManageTableEntity::getApplicationName,
            LinkManageTableEntity::getTenantId,
            LinkManageTableEntity::getEnvCode,
            LinkManageTableEntity::getUserId,
            LinkManageTableEntity::getFeatures
        );
        if (CollectionUtils.isNotEmpty(queryParam.getUserIdList())) {
            queryWrapper.in(LinkManageTableEntity::getUserId, queryParam.getUserIdList());
        }
        if (!StringUtils.isEmpty(queryParam.getSystemProcessName())) {
            queryWrapper.like(LinkManageTableEntity::getLinkName, queryParam.getSystemProcessName());
        }
        if (CollectionUtils.isNotEmpty(queryParam.getLinkIdList())) {
            queryWrapper.in(LinkManageTableEntity::getLinkId, queryParam.getLinkIdList());
        }
        queryWrapper.eq(LinkManageTableEntity::getIsDeleted, 0);
        List<LinkManageTableEntity> entityList = linkManageTableMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(entityList)) {
            linkManageResultList = entityList.stream().map(linkManageTableEntity -> {
                LinkManageResult linkManageResult = new LinkManageResult();
                linkManageResult.setLinkId(linkManageTableEntity.getLinkId());
                linkManageResult.setLinkName(linkManageTableEntity.getLinkName());
                linkManageResult.setApplicationName(linkManageTableEntity.getApplicationName());
                linkManageResult.setTenantId(linkManageTableEntity.getTenantId());
                linkManageResult.setEnvCode(linkManageTableEntity.getEnvCode());
                linkManageResult.setUserId(linkManageTableEntity.getUserId());
                linkManageResult.setFeatures(linkManageTableEntity.getFeatures());
                return linkManageResult;
            }).collect(Collectors.toList());
        }
        return linkManageResultList;
    }

    @Override
    public int insert(LinkManageCreateParam param) {
        LinkManageTableEntity entity = new LinkManageTableEntity();
        BeanUtils.copyProperties(param, entity);
        return linkManageTableMapper.insert(entity);
    }

    /**
     * 指定责任人-系统流程
     *
     * @param param
     * @return
     */
    @Override
    public int allocationUser(LinkManageUpdateParam param) {
        LambdaUpdateWrapper<LinkManageTableEntity> wrapper = new LambdaUpdateWrapper();
        wrapper.set(LinkManageTableEntity::getUserId, param.getUserId())
            .set(param.getDeptId() != null,LinkManageTableEntity::getDeptId, param.getDeptId())
            .eq(LinkManageTableEntity::getLinkId, param.getLinkId());
        return linkManageTableMapper.update(null, wrapper);
    }
}
