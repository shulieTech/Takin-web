/*
 * Copyright (c) 2021. Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.AppRemoteCallMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 无涯
 * @date 2021/5/29 12:15 上午
 */
@Component
public class AppRemoteCallDAOImpl extends ServiceImpl<AppRemoteCallMapper, AppRemoteCallEntity>
    implements AppRemoteCallDAO, MPUtil<AppRemoteCallEntity> {
    @Override
    public void insert(AppRemoteCallCreateParam param) {
        AppRemoteCallEntity entity = new AppRemoteCallEntity();
        BeanUtils.copyProperties(param, entity);
        if(param.getIsManual()){
            entity.setManualTag(1);
        }
        // 保底计算下
        entity.setMd5(RemoteCallUtils.buildRemoteCallName(entity.getAppName(),entity.getInterfaceName(),entity.getInterfaceType()));
        this.save(entity);
    }

    @Override
    public void batchInsert(List<AppRemoteCallCreateParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<AppRemoteCallEntity> entities = params.stream().map(param -> {
            AppRemoteCallEntity entity = new AppRemoteCallEntity();
            BeanUtils.copyProperties(param, entity);
            // 保底计算下
            entity.setMd5(RemoteCallUtils.buildRemoteCallName(entity.getAppName(),entity.getInterfaceName(),entity.getInterfaceType()));
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(entities);
    }

    @Override
    public void batchSaveOrUpdate(List<AppRemoteCallUpdateParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<AppRemoteCallEntity> entities = params.stream().map(param -> {
            AppRemoteCallEntity entity = new AppRemoteCallEntity();
            BeanUtils.copyProperties(param, entity);
            // 保底计算下
            entity.setMd5(RemoteCallUtils.buildRemoteCallName(entity.getAppName(),entity.getInterfaceName(),entity.getInterfaceType()));
            return entity;
        }).collect(Collectors.toList());
        this.saveOrUpdateBatch(entities);
    }

    @Override
    public void update(AppRemoteCallUpdateParam param) {
        AppRemoteCallEntity entity = new AppRemoteCallEntity();
        BeanUtils.copyProperties(param, entity);
        this.updateById(entity);
        // 同时更新md5
        AppRemoteCallEntity newEntity = this.getById(param.getId());
        entity.setMd5(RemoteCallUtils.buildRemoteCallName(newEntity.getAppName(),newEntity.getInterfaceName(),newEntity.getInterfaceType()));
        this.updateById(entity);
    }

    @Override
    public AppRemoteCallResult getResultById(Long id) {
        AppRemoteCallEntity entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        AppRemoteCallResult result = new AppRemoteCallResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    @Override
    public void deleteByApplicationIds(List<Long> applicationIds) {
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setApplicationIds(applicationIds);
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getAppRemoteCallEntityLambdaQueryWrapper(param);
        List<AppRemoteCallEntity> entities = this.list(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(entities)) {
            this.removeByIds(entities.stream().map(AppRemoteCallEntity::getId).collect(Collectors.toList()));
        }
    }

    @Override
    public List<AppRemoteCallResult> getList(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getAppRemoteCallEntityLambdaQueryWrapper(param);
        List<AppRemoteCallEntity> entities = this.list(lambdaQueryWrapper);
        return getAppRemoteCallResults(entities);
    }

    @Override
    public PagingList<AppRemoteCallResult> pagingList(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getAppRemoteCallEntityLambdaQueryWrapper(param);
        Page<AppRemoteCallEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        IPage<AppRemoteCallEntity> entityPageInfo = this.page(page, lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(entityPageInfo.getRecords())) {
            return PagingList.of(Lists.newArrayList(),entityPageInfo.getTotal());
        }
        return PagingList.of(getAppRemoteCallResults(entityPageInfo.getRecords()), entityPageInfo.getTotal());
    }

    private LambdaQueryWrapper<AppRemoteCallEntity> getAppRemoteCallEntityLambdaQueryWrapper(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper();
        if (param.getTenantId() != null) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getTenantId, param.getTenantId());
        }
        if(StringUtils.isNotBlank(param.getEnvCode())) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getEnvCode, param.getEnvCode());
        }
        if (CollectionUtils.isNotEmpty(param.getApplicationIds())) {
            lambdaQueryWrapper.in(AppRemoteCallEntity::getApplicationId, param.getApplicationIds());
        }
        if (param.getApplicationId() != null) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getApplicationId, param.getApplicationId());
        }
        if (param.getStatus() != null && param.getStatus() != 0) {
            if (param.getStatus() == 1) {
                lambdaQueryWrapper.eq(AppRemoteCallEntity::getType, 0);
            } else {
                lambdaQueryWrapper.in(AppRemoteCallEntity::getType,
                    // 1：配置状态 0：全部； 1：未配置（0） ；2：已配置(1,2,3)"
                    param.getType() == null ? Lists.newArrayList(1, 2, 3) : Lists.newArrayList(param.getType()));
            }
        } else {
            if (param.getType() != null) {
                lambdaQueryWrapper.eq(AppRemoteCallEntity::getType, param.getType());
            }
        }
        if (StringUtils.isNotBlank(param.getInterfaceName())) {
            // 模糊查询
            lambdaQueryWrapper.like(AppRemoteCallEntity::getInterfaceName, param.getInterfaceName());
        }

        if (param.getIsSynchronize() != null) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getIsSynchronize, param.getIsSynchronize());
        }
        lambdaQueryWrapper.orderByDesc(AppRemoteCallEntity::getGmtModified);
        return lambdaQueryWrapper;
    }

    @Override
    public List<AppRemoteCallResult> selectByAppNameUnderCurrentUser(String appName) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper();
        if (WebPluginUtils.checkUserPlugin()) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getTenantId, WebPluginUtils.traceTenantId());
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getEnvCode, WebPluginUtils.traceEnvCode());
        }
        lambdaQueryWrapper.eq(AppRemoteCallEntity::getAppName, appName);
        lambdaQueryWrapper.ne(AppRemoteCallEntity::getType, AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
        List<AppRemoteCallEntity> entities = this.list(lambdaQueryWrapper);
        return getAppRemoteCallResults(entities);
    }

    private List<AppRemoteCallResult> getAppRemoteCallResults(List<AppRemoteCallEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            AppRemoteCallResult result = new AppRemoteCallResult();
            BeanUtils.copyProperties(entity, result);
            result.setIsManual(entity.getManualTag() == 1);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateAppName(Long applicationId, String appName) {
        // 先获取
        LambdaQueryWrapper<AppRemoteCallEntity> queryWrapper  =this.getLambdaQueryWrapper();
        queryWrapper.eq(AppRemoteCallEntity::getApplicationId, applicationId);
        List<AppRemoteCallEntity> entities = this.list(queryWrapper);
        entities.forEach(e -> {
            e.setAppName(appName);
            e.setMd5(RemoteCallUtils.buildRemoteCallName(appName,e.getInterfaceName(),e.getInterfaceType()));
        });
        this.updateBatchById(entities);
    }

    @Override
    public Long getRecordCount(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getAppRemoteCallEntityLambdaQueryWrapper(param);
        return this.count(lambdaQueryWrapper);
    }

    @Override
    public List<AppRemoteCallResult> getPartRecord(AppRemoteCallQueryParam param, long start, int size) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getAppRemoteCallEntityLambdaQueryWrapper(param);
        lambdaQueryWrapper.last("limit "+start+","+size);
        List<AppRemoteCallEntity> list = this.list(lambdaQueryWrapper);
        return getAppRemoteCallResults(list);
    }


    /**
     * 根据id 批量逻辑删除
     *
     * @param ids
     */
    @Override
    public void batchLogicDelByIds(List<Long> ids) {
        List<AppRemoteCallEntity> entities = Lists.newArrayList();
        ids.forEach(id -> {
            AppRemoteCallEntity entity = new AppRemoteCallEntity();
            entity.setId(id);
            entity.setIsDeleted(1);
            entities.add(entity);
        });
        this.updateBatchById(entities);
    }

    /**
     * 批量保存
     *
     * @param list
     */
    @Override
    public void batchSave(List<AppRemoteCallResult> list) {
        List<AppRemoteCallEntity> collect = list.stream().
                map(appRemoteCallResult -> {
                    AppRemoteCallEntity newEntity = Convert.convert(AppRemoteCallEntity.class, appRemoteCallResult);
                    newEntity.setMd5(RemoteCallUtils.buildRemoteCallName(newEntity.getAppName(),newEntity.getInterfaceName(),newEntity.getInterfaceType()));
                    return newEntity;
                }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    /**
     * 查询全部有效的记录
     *
     * @return
     */
    @Override
    public List<AppRemoteCallResult> getAllRecord() {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(AppRemoteCallEntity::getIsDeleted,0);

        List<AppRemoteCallEntity> list = list(lambdaQueryWrapper);
        if(list.isEmpty()){
            return Collections.emptyList();
        }
        return list.stream()
                .map(entity -> Convert.convert(AppRemoteCallResult.class,entity)).collect(Collectors.toList());
    }

    @Override
    public List<AppRemoteCallResult> updateListSelective(Short type, List<Long> appIdList, List<Long> userIdList) {
        LambdaQueryWrapper<AppRemoteCallEntity> wrapper = this.getLambdaQueryWrapper()
            .in(AppRemoteCallEntity::getApplicationId, appIdList)
            .in(CollUtil.isNotEmpty(userIdList), AppRemoteCallEntity::getUserId, userIdList)
            .eq( WebPluginUtils.traceTenantId() != null, AppRemoteCallEntity::getTenantId, WebPluginUtils.traceTenantId())
            .eq( StringUtils.isNotBlank(WebPluginUtils.traceEnvCode()), AppRemoteCallEntity::getEnvCode, WebPluginUtils.traceEnvCode())
            ;
        List<AppRemoteCallEntity> appRemoteCallEntities = this.getBaseMapper().selectList(wrapper);
        List<AppRemoteCallEntity> updateAppRemoteCallEntityList = appRemoteCallEntities.stream().map(entity -> {
            AppRemoteCallEntity appRemoteCallEntity = new AppRemoteCallEntity();
            appRemoteCallEntity.setId(entity.getId());
            appRemoteCallEntity.setType(type.intValue());
            return appRemoteCallEntity;
        }).collect(Collectors.toList());
        this.updateBatchById(updateAppRemoteCallEntityList);
        if (CollUtil.isEmpty(appRemoteCallEntities)) {
            return Collections.emptyList();
        }
        return getAppRemoteCallResults(appRemoteCallEntities);
    }


    @Override
    public AppRemoteCallResult queryOne(String appName, Integer interfaceType, String interfaceName) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(AppRemoteCallEntity::getIsDeleted,0)
                .eq(AppRemoteCallEntity::getAppName,appName)
                .eq(AppRemoteCallEntity::getInterfaceType,interfaceType)
                .eq(AppRemoteCallEntity::getInterfaceName,interfaceName);
        AppRemoteCallEntity entity = this.getOne(lambdaQueryWrapper);
        if(Objects.isNull(entity)){
            return null;
        }
        return Convert.convert(AppRemoteCallResult.class,entity);
    }

    @Override
    public List<String> getRemoteCallMd5(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getAppRemoteCallEntityLambdaQueryWrapper(param);
        lambdaQueryWrapper.select(AppRemoteCallEntity::getMd5);
        List<AppRemoteCallEntity> entities = this.list(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(AppRemoteCallEntity::getMd5).collect(Collectors.toList());

    }
}
