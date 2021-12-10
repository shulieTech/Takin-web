package io.shulie.takin.web.data.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.domain.BusinessDomainDAO;
import io.shulie.takin.web.data.mapper.mysql.BusinessDomainMapper;
import io.shulie.takin.web.data.model.mysql.BusinessDomainEntity;
import io.shulie.takin.web.data.param.domain.BusinessDomainCreateParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainDeleteParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainQueryParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainUpdateParam;
import io.shulie.takin.web.data.result.BusinessDomainDetailResult;
import io.shulie.takin.web.data.result.BusinessDomainListResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 业务域表(BusinessDomain)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-12-07 15:59:49
 */
@Service
public class BusinessDomainDAOImpl extends ServiceImpl<BusinessDomainMapper, BusinessDomainEntity>
    implements BusinessDomainDAO, MPUtil<BusinessDomainEntity> {

    @Override
    public PagingList<BusinessDomainListResult> selectPage(BusinessDomainQueryParam queryParam) {
        LambdaQueryWrapper<BusinessDomainEntity> wrapper = this.getLambdaQueryWrapper();
        wrapper.eq(BusinessDomainEntity::getIsDeleted, 0);
        wrapper.orderByAsc(BusinessDomainEntity::getDomainOrder);
        IPage<BusinessDomainEntity> entityPageInfo = this.page(setPage(queryParam), wrapper);
        if (CollectionUtils.isEmpty(entityPageInfo.getRecords())) {
            return PagingList.of(Lists.newArrayList(), entityPageInfo.getTotal());
        }
        List<BusinessDomainListResult> results = entityPageInfo.getRecords().stream().map(entity -> {
            BusinessDomainListResult result = new BusinessDomainListResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
        return PagingList.of(results, entityPageInfo.getTotal());
    }

    @Override
    public int insert(BusinessDomainCreateParam createParam) {
        BusinessDomainEntity entity = new BusinessDomainEntity();
        BeanUtils.copyProperties(createParam, entity);
        return this.baseMapper.insert(entity);
    }

    @Override
    public int update(BusinessDomainUpdateParam updateParam) {
        if (!Objects.isNull(updateParam.getId())) {
            BusinessDomainEntity entity = new BusinessDomainEntity();
            BeanUtils.copyProperties(updateParam, entity);
            return this.baseMapper.updateById(entity);
        }
        return 0;
    }

    @Override
    public int delete(BusinessDomainDeleteParam deleteParam) {
        if (CollectionUtils.isNotEmpty(deleteParam.getIds())) {
            return this.baseMapper.deleteBatchIds(deleteParam.getIds());
        }
        return 0;
    }

    @Override
    public List<BusinessDomainListResult> selectList(BusinessDomainQueryParam queryParam) {
        LambdaQueryWrapper<BusinessDomainEntity> queryWrapper = this.getLambdaQueryWrapper()
            .eq(BusinessDomainEntity::getIsDeleted, 0);
        if (StringUtils.isNotBlank(queryParam.getName())) {
            queryWrapper.eq(BusinessDomainEntity::getName, queryParam.getName());
        }
        if (CollectionUtils.isNotEmpty(queryParam.getIds())) {
            queryWrapper.in(BusinessDomainEntity::getId, queryParam.getIds());
        }
        if (CollectionUtils.isNotEmpty(queryParam.getDomainCodes())) {
            queryWrapper.in(BusinessDomainEntity::getDomainCode, queryParam.getDomainCodes());
        }
        queryWrapper.orderByAsc(BusinessDomainEntity::getDomainOrder);
        List<BusinessDomainEntity> entities = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(entity -> {
            BusinessDomainListResult result = new BusinessDomainListResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public BusinessDomainDetailResult selectById(Long id) {
        BusinessDomainEntity entity = this.baseMapper.selectById(id);
        if (Objects.isNull(entity)) {
            return null;
        }
        BusinessDomainDetailResult result = new BusinessDomainDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public int selectMaxDomainCode() {
        BusinessDomainEntity entity = this.baseMapper.selectOne(this.getLimitOneLambdaQueryWrapper()
            .eq(BusinessDomainEntity::getIsDeleted, 0)
            .orderByDesc(BusinessDomainEntity::getDomainCode));
        if (Objects.nonNull(entity)) {
            return entity.getDomainCode();
        }
        return 0;
    }
}

