package io.shulie.takin.web.data.dao.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsDbManageMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
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
    public List<ApplicationDsDbManageDetailResult> selectList(Long appId) {
        LambdaQueryWrapper<ApplicationDsDbManageEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(ApplicationDsDbManageEntity::getApplicationId,appId)
                .eq(ApplicationDsDbManageEntity::getIsDeleted,0)
                .eq(ApplicationDsDbManageEntity::getStatus,0);
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
        entity.setId(id);
        this.updateById(entity);
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
        return entities.stream().map(entity -> this.getApplicationDsDbManageDetailResult(entity)).collect(Collectors.toList());
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

