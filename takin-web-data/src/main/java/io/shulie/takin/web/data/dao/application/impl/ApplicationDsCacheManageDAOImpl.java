package io.shulie.takin.web.data.dao.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.ApplicationDsCacheManageDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsCacheManageMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 缓存影子库表配置表(ApplicationDsCacheManage)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-30 10:59:33
 */
@Service
public class ApplicationDsCacheManageDAOImpl  extends ServiceImpl<ApplicationDsCacheManageMapper, ApplicationDsCacheManageEntity> implements ApplicationDsCacheManageDAO, MPUtil<ApplicationDsCacheManageEntity> {



    @Override
    public List<ApplicationDsCacheManageDetailResult> selectList(Long appId) {
        LambdaQueryWrapper<ApplicationDsCacheManageEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(ApplicationDsCacheManageEntity::getApplicationId,appId)
                .eq(ApplicationDsCacheManageEntity::getIsDeleted,0)
                .eq(ApplicationDsCacheManageEntity::getStatus,0);
        List<ApplicationDsCacheManageEntity> list = this.list(lambdaQueryWrapper);

        return getApplicationDsCacheManageDetailResults(list);
    }


    @Override
    public void batchSave(List<ApplicationDsCacheManageDetailResult> list) {

       this.saveBatch(this.getApplicationDsCacheManageEntitys(list));
    }

    @Override
    public ApplicationDsCacheManageDetailResult selectOneById(Long id) {
        ApplicationDsCacheManageEntity entity = this.getById(id);
        return this.getApplicationDsCacheManageDetailResult(entity);
    }

    @Override
    public void updateById(Long id, ApplicationDsCacheManageEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    private ApplicationDsCacheManageDetailResult getApplicationDsCacheManageDetailResult(ApplicationDsCacheManageEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        ApplicationDsCacheManageDetailResult result = new ApplicationDsCacheManageDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    private List<ApplicationDsCacheManageDetailResult> getApplicationDsCacheManageDetailResults(List<ApplicationDsCacheManageEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> this.getApplicationDsCacheManageDetailResult(entity)).collect(Collectors.toList());
    }

    private ApplicationDsCacheManageEntity getApplicationDsCacheManageEntity(ApplicationDsCacheManageDetailResult result) {
        if (Objects.isNull(result)) {
            return null;
        }
        ApplicationDsCacheManageEntity entity = new ApplicationDsCacheManageEntity();
        BeanUtils.copyProperties( result,entity);
        return entity;
    }

    private List<ApplicationDsCacheManageEntity> getApplicationDsCacheManageEntitys(List<ApplicationDsCacheManageDetailResult> result) {
        if (CollectionUtils.isEmpty(result)) {
            return Lists.newArrayList();
        }
        return result.stream().map(detail -> this.getApplicationDsCacheManageEntity(detail)).collect(Collectors.toList());
    }
}

