package io.shulie.takin.web.data.dao.application.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsDbManageMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * db连接池影子库表配置表(ApplicationDsDbManage)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-30 10:59:59
 */
@Service
public class ApplicationDsDbManageDAOImpl  extends ServiceImpl<ApplicationDsDbManageMapper, ApplicationDsDbManageEntity> implements ApplicationDsDbManageDAO, MPUtil<ApplicationDsDbManageEntity> {

    @Override
    public List<ApplicationDsDbManageDetailResult> selectList(ApplicationDsQueryParam param) {
        LambdaQueryWrapper<ApplicationDsDbManageEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper();
        if (!Objects.isNull(param.getApplicationId())) {
            lambdaQueryWrapper.eq(ApplicationDsDbManageEntity::getApplicationId, param.getApplicationId());
        }
        if (!Objects.isNull(param.getStatus())) {
            lambdaQueryWrapper.eq(ApplicationDsDbManageEntity::getStatus, param.getStatus());
        }
        if (!Objects.isNull(param.getIsDeleted())) {
            lambdaQueryWrapper.eq(ApplicationDsDbManageEntity::getIsDeleted, param.getIsDeleted());
        }else{
            lambdaQueryWrapper.eq(ApplicationDsDbManageEntity::getIsDeleted, 0);
        }
        if (CollectionUtils.isNotEmpty(param.getUserIdList())) {
            lambdaQueryWrapper.in(ApplicationDsDbManageEntity::getUserId, param.getUserIdList());
        }
        lambdaQueryWrapper.orderByDesc(ApplicationDsDbManageEntity::getGmtUpdate);

        List<ApplicationDsDbManageEntity> list = this.list(lambdaQueryWrapper);

        return getApplicationDsDbManageDetailResults(list);
    }

    @Override
    public void batchSave(List<ApplicationDsDbManageDetailResult> list) {
        this.saveBatch(this.getEntitys(list));
    }

    @Override
    public ApplicationDsDbManageDetailResult selectOneById(Long id) {
        ApplicationDsDbManageEntity entity = this.getById(id);
        return this.getApplicationDsDbManageDetailResult(entity);
    }

    @Override
    public void updateById(Long id, ApplicationDsDbManageEntity entity) {
        LambdaQueryWrapper<ApplicationDsDbManageEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper();
        lambdaQueryWrapper.eq(ApplicationDsDbManageEntity::getId,id);
        this.update(entity,lambdaQueryWrapper);
    }

    @Override
    public void removeRecord(Long id) {
        this.removeById(id);
    }

    @Override
    public void saveOne(ApplicationDsDbManageEntity entity) {
        this.save(entity);
    }

    private ApplicationDsDbManageDetailResult getApplicationDsDbManageDetailResult(ApplicationDsDbManageEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        ApplicationDsDbManageDetailResult result = new ApplicationDsDbManageDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    private List<ApplicationDsDbManageDetailResult> getApplicationDsDbManageDetailResults(List<ApplicationDsDbManageEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(this::getApplicationDsDbManageDetailResult).collect(Collectors.toList());
    }

    private ApplicationDsDbManageEntity getEntity(ApplicationDsDbManageDetailResult result) {
        if (Objects.isNull(result)) {
            return null;
        }
        ApplicationDsDbManageEntity entity = new ApplicationDsDbManageEntity();
        BeanUtils.copyProperties( result,entity);
        return result;
    }

    private List<ApplicationDsDbManageEntity> getEntitys(List<ApplicationDsDbManageDetailResult> results) {
        if (CollectionUtils.isEmpty(results)) {
            return Lists.newArrayList();
        }
        return results.stream().map(result -> this.getEntity(result)).collect(Collectors.toList());
    }
}

