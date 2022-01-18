package io.shulie.takin.web.data.dao.datasource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.mapper.mysql.TakinDbresourceMapper;
import io.shulie.takin.web.data.model.mysql.TakinDbresourceEntity;
import io.shulie.takin.web.data.param.datasource.DataSourceCreateParam;
import io.shulie.takin.web.data.param.datasource.DataSourceDeleteParam;
import io.shulie.takin.web.data.param.datasource.DataSourceQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceSingleQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceUpdateParam;
import io.shulie.takin.web.data.result.datasource.DataSourceResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/12/30 10:21 上午
 */
@Slf4j
@Component
public class DataSourceDAOImpl implements DataSourceDAO {

    @Autowired
    private TakinDbresourceMapper datasourceMapper;

    @Override
    public PagingList<DataSourceResult> selectPage(DataSourceQueryParam queryParam) {
        LambdaQueryWrapper<TakinDbresourceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TakinDbresourceEntity::getId,
            TakinDbresourceEntity::getType,
            TakinDbresourceEntity::getName,
            TakinDbresourceEntity::getJdbcUrl,
            TakinDbresourceEntity::getUsername,
            TakinDbresourceEntity::getCreateTime,
            TakinDbresourceEntity::getUpdateTime);
        if (!Objects.isNull(queryParam.getType())) {
            wrapper.eq(TakinDbresourceEntity::getType, queryParam.getType());
        }
        if (StringUtils.isNotBlank(queryParam.getName())) {
            wrapper.like(TakinDbresourceEntity::getName, "\\"+queryParam.getName());
        }
        if (StringUtils.isNotBlank(queryParam.getJdbcUrl())) {
            wrapper.like(TakinDbresourceEntity::getJdbcUrl, "\\"+queryParam.getJdbcUrl());
        }
        if (CollectionUtils.isNotEmpty(queryParam.getDataSourceIdList())) {
            wrapper.in(TakinDbresourceEntity::getId, queryParam.getDataSourceIdList());
        }
        // 数据权限
        if(CollectionUtils.isNotEmpty(WebPluginUtils.getQueryAllowUserIdList())) {
            wrapper.in(TakinDbresourceEntity::getUserId, WebPluginUtils.getQueryAllowUserIdList());
        }
        Page<TakinDbresourceEntity> page = new Page<>(queryParam.getCurrent(), queryParam.getPageSize());
        wrapper.orderByDesc(TakinDbresourceEntity::getUpdateTime);

        IPage<TakinDbresourceEntity> takinResourceEntityPage = datasourceMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(takinResourceEntityPage.getRecords())) {
            return PagingList.of(Lists.newArrayList(),takinResourceEntityPage.getTotal());
        }
        List<DataSourceResult> dataSourceResultList = takinResourceEntityPage.getRecords().stream().map(entity -> {
            DataSourceResult dataSourceResult = new DataSourceResult();
            BeanUtils.copyProperties(entity, dataSourceResult);
            return dataSourceResult;
        }).collect(Collectors.toList());
        return PagingList.of(dataSourceResultList, page.getTotal());
    }

    @Override
    public int insert(DataSourceCreateParam createParam) {
        TakinDbresourceEntity entity = new TakinDbresourceEntity();
        BeanUtils.copyProperties(createParam, entity);
        return datasourceMapper.insert(entity);
    }

    @Override
    public int update(DataSourceUpdateParam updateParam) {
        if (!Objects.isNull(updateParam.getId())) {
            TakinDbresourceEntity entity = new TakinDbresourceEntity();
            BeanUtils.copyProperties(updateParam, entity);
            return datasourceMapper.updateById(entity);
        }
        return 0;
    }

    @Override
    public int delete(DataSourceDeleteParam deleteParam) {
        if (CollectionUtils.isNotEmpty(deleteParam.getIdList())) {
            return datasourceMapper.deleteBatchIds(deleteParam.getIdList());
        }
        return 0;
    }

    @Override
    public DataSourceResult selectSingle(DataSourceSingleQueryParam queryParam) {
        LambdaQueryWrapper<TakinDbresourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!Objects.isNull(queryParam.getId())) {
            queryWrapper.eq(TakinDbresourceEntity::getId, queryParam.getId());
        }
        if (StringUtils.isNotBlank(queryParam.getName())) {
            queryWrapper.eq(TakinDbresourceEntity::getName, queryParam.getName());
        }
        if (StringUtils.isNotBlank(queryParam.getJdbcUrl())) {
            queryWrapper.eq(TakinDbresourceEntity::getJdbcUrl, queryParam.getJdbcUrl());
        }

        queryWrapper.eq(TakinDbresourceEntity::getIsDeleted, 0);
        List<TakinDbresourceEntity> datasourceEntityList = datasourceMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(datasourceEntityList)) {
            if (datasourceEntityList.size() > 1) {
                log.warn("存在多个同名数据源，请及时处理脏数据");
            }
            TakinDbresourceEntity datasourceEntity = datasourceEntityList.get(0);
            DataSourceResult dataSourceResult = new DataSourceResult();
            dataSourceResult.setId(datasourceEntity.getId());
            dataSourceResult.setType(datasourceEntity.getType());
            dataSourceResult.setName(datasourceEntity.getName());
            dataSourceResult.setJdbcUrl(datasourceEntity.getJdbcUrl());
            dataSourceResult.setUsername(datasourceEntity.getUsername());
            dataSourceResult.setPassword(datasourceEntity.getPassword());
            dataSourceResult.setCreateTime(datasourceEntity.getCreateTime());
            dataSourceResult.setUpdateTime(datasourceEntity.getUpdateTime());
            dataSourceResult.setUserId(datasourceEntity.getUserId());
            return dataSourceResult;
        }
        return null;
    }

    @Override
    public List<DataSourceResult> selectList(DataSourceQueryParam queryParam) {
        LambdaQueryWrapper<TakinDbresourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(queryParam.getDataSourceIdList())) {
            queryWrapper.in(TakinDbresourceEntity::getId, queryParam.getDataSourceIdList());
            queryWrapper.eq(TakinDbresourceEntity::getIsDeleted, 0);
        }
        queryWrapper.orderByDesc(TakinDbresourceEntity::getUpdateTime);
        List<TakinDbresourceEntity> datasourceEntityList = datasourceMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(datasourceEntityList)) {
            return datasourceEntityList.stream().map(entity -> {
                DataSourceResult dataSourceResult = new DataSourceResult();
                BeanUtils.copyProperties(entity, dataSourceResult);
                return dataSourceResult;
            }).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
