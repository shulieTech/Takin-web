package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.AppRemoteCallMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

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
            return entity;
        }).collect(Collectors.toList());
        this.saveOrUpdateBatch(entities);
    }

    @Override
    public void update(AppRemoteCallUpdateParam param) {
        AppRemoteCallEntity entity = new AppRemoteCallEntity();
        BeanUtils.copyProperties(param, entity);
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
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = getAppRemoteCallEntityLambdaQueryWrapper(param);
        List<AppRemoteCallEntity> entities = this.list(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(entities)) {
            this.removeByIds(entities.stream().map(AppRemoteCallEntity::getId).collect(Collectors.toList()));
        }
    }

    @Override
    public List<AppRemoteCallResult> getList(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = getAppRemoteCallEntityLambdaQueryWrapper(param);
        List<AppRemoteCallEntity> entities = this.list(lambdaQueryWrapper);
        return getAppRemoteCallResults(entities);
    }

    @Override
    public PagingList<AppRemoteCallResult> pagingList(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = getAppRemoteCallEntityLambdaQueryWrapper(param);
        Page<AppRemoteCallEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        IPage<AppRemoteCallEntity> entityPageInfo = this.page(page, lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(entityPageInfo.getRecords())) {
            return PagingList.of(Lists.newArrayList(),entityPageInfo.getTotal());
        }
        return PagingList.of(getAppRemoteCallResults(entityPageInfo.getRecords()), entityPageInfo.getTotal());
    }

    private LambdaQueryWrapper<AppRemoteCallEntity> getAppRemoteCallEntityLambdaQueryWrapper(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper();
        if (WebPluginUtils.checkUserData() && WebPluginUtils.getCustomerId() != null) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getCustomerId, WebPluginUtils.getCustomerId());
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
        if (WebPluginUtils.checkUserData()) {
            lambdaQueryWrapper.eq(AppRemoteCallEntity::getCustomerId, WebPluginUtils.getCustomerId());
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
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateAppName(Long applicationId, String appName) {
        LambdaUpdateWrapper<AppRemoteCallEntity> wrapper = this.getLambdaUpdateWrapper();
        wrapper.eq(AppRemoteCallEntity::getApplicationId, applicationId);
        wrapper.set(AppRemoteCallEntity::getAppName, appName);
        this.update(wrapper);
    }

    @Override
    public Long getRecordCount(AppRemoteCallQueryParam param) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = getAppRemoteCallEntityLambdaQueryWrapper(param);
        return Long.valueOf(this.count(lambdaQueryWrapper));
    }

    @Override
    public List<AppRemoteCallResult> getPartRecord(AppRemoteCallQueryParam param, long start, int size) {
        LambdaQueryWrapper<AppRemoteCallEntity> lambdaQueryWrapper = getAppRemoteCallEntityLambdaQueryWrapper(param);
        lambdaQueryWrapper.last("limit "+start+","+size);
        List<AppRemoteCallEntity> list = this.list(lambdaQueryWrapper);
        return getAppRemoteCallResults(list);
    }
}
