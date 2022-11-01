package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbTableDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsDbTableMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsDbTableDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 业务数据库表(ApplicationDsDbTable)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-09-15 17:21:41
 */
@Service
public class ApplicationDsDbTableDAOImpl extends ServiceImpl<ApplicationDsDbTableMapper, ApplicationDsDbTableEntity>
        implements ApplicationDsDbTableDAO, MPUtil<ApplicationDsDbTableEntity> {

    @Override
    public List<ApplicationDsDbTableDetailResult> getList(String url, Long appId, String userName) {
        LambdaQueryWrapper<ApplicationDsDbTableEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(ApplicationDsDbTableEntity::getAppId, appId)
                .eq(ApplicationDsDbTableEntity::getUrl, url)
                .eq(ApplicationDsDbTableEntity::getUserName, userName)
                .eq(ApplicationDsDbTableEntity::getIsDeleted, 0);
        List<ApplicationDsDbTableEntity> list = this.list(lambdaQueryWrapper);
        return getApplicationDsDbTableDetailResults(list);
    }

    @Override
    public List<ApplicationDsDbTableDetailResult> queryList(String url, Long appId, String userName, String bizTable) {
        LambdaQueryWrapper<ApplicationDsDbTableEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(ApplicationDsDbTableEntity::getAppId, appId)
                .eq(ApplicationDsDbTableEntity::getUrl, url)
                .eq(ApplicationDsDbTableEntity::getUserName, userName)
                .eq(ApplicationDsDbTableEntity::getIsDeleted, 0);
        if (StringUtils.isNotBlank(bizTable)) {
            lambdaQueryWrapper.eq(ApplicationDsDbTableEntity::getBizTable, bizTable);
        }
        List<ApplicationDsDbTableEntity> list = this.list(lambdaQueryWrapper);
        return getApplicationDsDbTableDetailResults(list);
    }


    @Override
    public void batchSave(List<ApplicationDsDbTableDetailResult> list) {
        List<ApplicationDsDbTableEntity> entities = this.getEntitys(list);
        this.saveBatch(entities);
    }

    @Override
    public void saveOrUpdate(List<ApplicationDsDbTableDetailResult> list) {
    }

    @Override
    public List<ApplicationDsDbTableEntity> batchSave_ext(List<ApplicationDsDbTableDetailResult> list) {
        List<ApplicationDsDbTableEntity> entities = this.getEntitys(list);
        this.saveBatch(entities);
        return entities;
    }

    @Override
    public void batchDeleted(List<ApplicationDsDbTableDetailResult> list) {
        List<ApplicationDsDbTableEntity> entities = this.getEntitys(list);
        List<Long> ids = CollStreamUtil.toList(entities, ApplicationDsDbTableEntity::getId);
        this.removeByIds(ids);
    }

    @Override
    public void batchDeleted_V2(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public ApplicationDsDbTableEntity getOne(Long id) {
        return this.getById(id);
    }

    @Override
    public void update_v2(ApplicationDsDbTableEntity entity) {
        this.updateById(entity);
    }

    private ApplicationDsDbTableDetailResult getApplicationDsDbTableDetailResult(ApplicationDsDbTableEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        ApplicationDsDbTableDetailResult result = new ApplicationDsDbTableDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    private List<ApplicationDsDbTableDetailResult> getApplicationDsDbTableDetailResults(List<ApplicationDsDbTableEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(this::getApplicationDsDbTableDetailResult).collect(Collectors.toList());
    }

    private ApplicationDsDbTableEntity getEntity(ApplicationDsDbTableDetailResult result) {
        if (Objects.isNull(result)) {
            return null;
        }
        ApplicationDsDbTableEntity entity = new ApplicationDsDbTableEntity();
        BeanUtils.copyProperties(result, entity);
        return entity;
    }

    private List<ApplicationDsDbTableEntity> getEntitys(List<ApplicationDsDbTableDetailResult> results) {
        if (CollectionUtils.isEmpty(results)) {
            return Lists.newArrayList();
        }
        return results.stream().map(this::getEntity).collect(Collectors.toList());
    }
}

